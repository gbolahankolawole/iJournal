package com.example.kolawole.ijournal;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import com.example.kolawole.ijournal.dbfiles.AppDatabase;
import com.example.kolawole.ijournal.dbfiles.JournalEntry;

import java.util.Date;

public class AddJournalEntry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";
    // Extra for the entry ID to be received after rotation
    public static final String INSTANCE_ENTRY_ID = "instanceEntryId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_ENTRY_ID = -1;

    // Fields for views
    EditText mEditText;
    EditText tEditText;
    Spinner spinner;
    String sel;
    private int mEntryId = DEFAULT_ENTRY_ID;

    // Member variable for the Database
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal_entry);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ENTRY_ID)) {
            mEntryId = savedInstanceState.getInt(INSTANCE_ENTRY_ID, DEFAULT_ENTRY_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {
            //mButton.setText(R.string.update_button);
            if (mEntryId == DEFAULT_ENTRY_ID) {
                // populate the UI
                mEntryId = intent.getIntExtra(EXTRA_ENTRY_ID, DEFAULT_ENTRY_ID);

                AddJournalEntryViewModelFactory factory = new AddJournalEntryViewModelFactory(mDb, mEntryId);
                // Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                // for that use the factory created above AddTaskViewModel
                final AddJournalEntryViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddJournalEntryViewModel.class);

                // Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(journalEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_ENTRY_ID, mEntryId);
        super.onSaveInstanceState(outState);
    }


    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextJournalEntry);
        tEditText = findViewById(R.id.editTextJournalTitle);
        spinner = findViewById(R.id.drop_down_tag_list);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param entry the JournalEntry to populate the UI
     */
    private void populateUI(JournalEntry entry) {
        if (entry == null) {
            return;
        }

        tEditText.setText(entry.getTitle());
        mEditText.setText(entry.getDescription());
        //setTagInViews(entry.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our visualizer_menu layout to this menu */
        inflater.inflate(R.menu.edit_entry_menu, menu);
        /* Return true so that the visualizer_menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_entry) {
            saveEntry();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void saveEntry() {
        String description = mEditText.getText().toString();
        String title = tEditText.getText().toString();

        //int priority = getPriorityFromViews();
        Date date = new Date();

        final JournalEntry entry = new JournalEntry(title, description, sel, date);
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mEntryId == DEFAULT_ENTRY_ID) {
                    // insert new task
                    mDb.entryDAO().insertEntry(entry);
                }
                else {
                    //update task
                    entry.setId(mEntryId);
                    mDb.entryDAO().updateEntry(entry);
                }
                finish();
            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        sel=adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
