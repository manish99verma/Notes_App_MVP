package com.example.notesappmvp.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Entity(tableName = "note_table")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    private int id;

    @SerializedName("firebase_id")
    private String firebase_id;

    @SerializedName("title")
    private String title;

    @SerializedName("date")
    private String date;

    @SerializedName("msg")
    private String msg;

    public Note(String firebase_id, String title, String date, String msg) {
        this.firebase_id = firebase_id;
        this.title = title;
        this.date = date;
        this.msg = msg;
    }

    @Ignore
    public Note(String title, String date, String msg) {
        this.title = title;
        this.date = date;
        this.msg = msg;
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

    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFirebase_id() {
        return firebase_id;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }
}
