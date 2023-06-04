package com.example.notesappmvp.Preseter.main;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;

import java.util.List;


public class MainActivityPresenter implements MainActivityContact.Presenter {
    private final NotesRepository notesRepository;

    public MainActivityPresenter(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notesRepository.getSavedNotes();
    }

    @Override
    public LiveData<List<Note>> getSearchedNotes(String query) {
        return notesRepository.getSearchedNotes(query);
    }
}
