package com.example.notesappmvp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.data_source.NotesLocalDataSource;
import com.example.notesappmvp.data.model.Note;

import java.util.List;

public class NotesRepositoryImpl implements NotesRepository {
    NotesLocalDataSource localDataSource;

    public NotesRepositoryImpl(NotesLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public void saveNote(Note note, NotesRepository.OnNoteSavedEvent event) {
        localDataSource.saveNote(note, event);
    }

    @Override
    public void deleteNote(Note note) {
        localDataSource.deleteNote(note);
    }

    @Override
    public LiveData<List<Note>> getSavedNotes() {
        return localDataSource.getAllNotes();
    }

    @Override
    public LiveData<List<Note>> getSearchedNotes(String query) {
        return localDataSource.getAllNotes();
    }
}
