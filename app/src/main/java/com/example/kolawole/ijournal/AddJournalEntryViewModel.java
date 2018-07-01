package com.example.kolawole.ijournal;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.kolawole.ijournal.dbfiles.AppDatabase;
import com.example.kolawole.ijournal.dbfiles.JournalEntry;

public class AddJournalEntryViewModel extends ViewModel {

    // Add a task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<JournalEntry> entry;

    // Create a constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor should receive the database and the taskId
    public AddJournalEntryViewModel(AppDatabase database, int entryId) {
        entry = database.entryDAO().loadEntryById(entryId);
    }

    // Create a getter for the task variable
    public LiveData<JournalEntry> getTask() {
        return entry;
    }
}
