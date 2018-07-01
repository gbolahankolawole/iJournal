package com.example.kolawole.ijournal;

import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.kolawole.ijournal.AppUtils.NoticeDialog;
import com.example.kolawole.ijournal.dbfiles.AppDatabase;
import com.example.kolawole.ijournal.dbfiles.JournalEntry;

import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class JournalEntries extends AppCompatActivity implements EntryAdapter.ItemClickListener,NoticeDialog.NoticeDialogListener{

    // Constant for logging
    private static final String TAG = JournalEntries.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private EntryAdapter mAdapter;
    private boolean isEntryDeleted;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entries);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerView_Entries);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new EntryAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                showNoticeDialog();

                if(isEntryDeleted){
                    // Here is where you'll implement swipe to delete
                    AppExecutor.getInstance().diskIO().execute(new Runnable() {

                        @Override
                        public void run() {
                            int position = viewHolder.getAdapterPosition();
                            List<JournalEntry> entries = mAdapter.getEntries();
                            mDb.entryDAO().deleteEntry(entries.get(position));
                        }
                    });
                }
            }

        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addEntryIntent = new Intent(JournalEntries.this, AddJournalEntry.class);
                startActivity(addEntryIntent);
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        JournalEntriesViewModel viewModel = ViewModelProviders.of(this).get(JournalEntriesViewModel.class);
        viewModel.getEntries().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setEntries(journalEntries);
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(JournalEntries.this, AddJournalEntry.class);
        intent.putExtra(AddJournalEntry.EXTRA_ENTRY_ID, itemId);
        startActivity(intent);
    }

    /**
     * Methods for setting up the menu
     **/

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new NoticeDialog();
        dialog.show(getFragmentManager(), "NoticeDialog");
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogFragment.NoticeDialogListener interface
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        isEntryDeleted=true;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        isEntryDeleted=false;
    }
}
