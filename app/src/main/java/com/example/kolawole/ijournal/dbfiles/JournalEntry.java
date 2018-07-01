package com.example.kolawole.ijournal.dbfiles;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "entry")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private String tag;
    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    @Ignore
    public JournalEntry(String title, String description, String tag, Date updatedAt) {
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.updatedAt = updatedAt;
    }

    public JournalEntry(int id, String title, String description, String tag, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tag = tag;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
