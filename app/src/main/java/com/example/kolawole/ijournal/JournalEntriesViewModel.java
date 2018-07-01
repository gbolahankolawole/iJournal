package com.example.kolawole.ijournal;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.kolawole.ijournal.dbfiles.AppDatabase;
import com.example.kolawole.ijournal.dbfiles.JournalEntry;

import java.util.List;

public class JournalEntriesViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = JournalEntriesViewModel.class.getSimpleName();

    private LiveData<List<JournalEntry>> entries;

    public JournalEntriesViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        entries = database.entryDAO().loadAllEntries();
    }

    public LiveData<List<JournalEntry>> getEntries() {
        return entries;
    }
}
