package com.example.kolawole.ijournal;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kolawole.ijournal.dbfiles.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<JournalEntry> mJournalEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public EntryAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.journal_entry_list_item, parent, false);

        return new EntryViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(EntryViewHolder holder, int position) {
        // Determine the values of the wanted data
        JournalEntry journalEntry = mJournalEntries.get(position);
        String title = journalEntry.getTitle();
        String description = journalEntry.getDescription();
        String tag = journalEntry.getTag();
        String updatedAt = dateFormat.format(journalEntry.getUpdatedAt());

        //Set values
        holder.entryTitle.setText(title);
        holder.entryDescription.setText(description);
        holder.entryUpdatedAt.setText(updatedAt);

        // Programmatically set the text and color for the priority TextView
        Character tagChar = tag.toUpperCase().charAt(0);
        holder.tagTextView.setText(tagChar+"");

        GradientDrawable tagCircle = (GradientDrawable) holder.tagTextView.getBackground();
        // Get the appropriate background color based on the tag
        int priorityColor = getTagColor(tagChar);
        tagCircle.setColor(priorityColor);
    }

    /*
    Helper method for selecting the correct tag view color.
    */
    private int getTagColor(int tagColor) {
        int color = 0;

        switch (tagColor) {
            case 'U':
                color = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 'W':
                color = ContextCompat.getColor(mContext, R.color.materialPurple);
                break;
            case 'P':
                color = ContextCompat.getColor(mContext, R.color.materialIndigo);
                break;
            case 'F':
                color = ContextCompat.getColor(mContext, R.color.materialLightBlue);
                break;
            case 'S':
                color = ContextCompat.getColor(mContext, R.color.materialGreen);
                break;
            case 'B':
                color = ContextCompat.getColor(mContext, R.color.materialBrown);
                break;
            case 'R':
                color = ContextCompat.getColor(mContext, R.color.materialGrey);
                break;
            case 'I':
                color = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return color;
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mJournalEntries == null) {
            return 0;
        }
        return mJournalEntries.size();
    }

    public List<JournalEntry> getEntries() {
        return mJournalEntries;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setEntries(List<JournalEntry> journalEntries) {
        mJournalEntries = journalEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    class EntryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView entryTitle;
        TextView entryDescription;
        TextView entryUpdatedAt;
        TextView tagTextView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public EntryViewHolder(View itemView) {
            super(itemView);

            entryTitle = itemView.findViewById(R.id.entryTitle);
            entryDescription = itemView.findViewById(R.id.entryDescription);
            entryUpdatedAt = itemView.findViewById(R.id.entryUpdatedAt);
            tagTextView = itemView.findViewById(R.id.tagTextView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mJournalEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}
