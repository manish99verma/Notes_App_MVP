package com.example.notesappmvp.data.repository;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.model.Note;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public interface NotesRepository {
    void setCurrentUser(FirebaseUser user);
    void newLogin(FirebaseUser user);
    void saveNote(Note note, NotesRepository.OnNoteSavedEvent event);

    void deleteNote(Note note);

    LiveData<List<Note>> getSavedNotes();

    LiveData<List<Note>> getSearchedNotes(String query);

    interface OnNoteSavedEvent {
        void onNoteSaved(long id);
    }
}
