package com.example.notesappmvp.data.data_source.web;

import com.example.notesappmvp.data.model.Note;
import com.google.firebase.auth.FirebaseUser;

public interface FirebaseDataSource {
    void syncNotes();

    void saveNote(Note note);

    void deleteNote(Note note);

    void newLogin(FirebaseUser user);

    void setCurrentUser(FirebaseUser user);
}
