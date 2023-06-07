package com.example.notesappmvp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.data_source.local.NotesLocalDataSource;
import com.example.notesappmvp.data.data_source.web.FirebaseDataSource;
import com.example.notesappmvp.data.model.Note;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class NotesRepositoryImpl implements NotesRepository {
    NotesLocalDataSource localDataSource;
    FirebaseDataSource firebaseDataSource;

    public NotesRepositoryImpl(NotesLocalDataSource localDataSource, FirebaseDataSource firebaseDataSource) {
        this.localDataSource = localDataSource;
        this.firebaseDataSource = firebaseDataSource;
    }

    @Override
    public void setCurrentUser(FirebaseUser user) {
        firebaseDataSource.setCurrentUser(user);
    }

    @Override
    public void newLogin(FirebaseUser user) {
        firebaseDataSource.newLogin(user);
    }

    @Override
    public void saveNote(Note note, NotesRepository.OnNoteSavedEvent event) {
        firebaseDataSource.saveNote(note);
        localDataSource.saveNote(note, event);
    }

    @Override
    public void deleteNote(Note note) {
        firebaseDataSource.deleteNote(note);
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
