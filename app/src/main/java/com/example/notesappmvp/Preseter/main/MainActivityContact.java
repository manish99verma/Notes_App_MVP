package com.example.notesappmvp.Preseter.main;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.model.Note;

import java.util.List;

public interface MainActivityContact {
    interface Presenter {
        LiveData<List<Note>> getAllNotes();

        LiveData<List<Note>> filterNotes(String query, List<Note> list);
    }
}
