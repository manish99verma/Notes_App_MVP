package com.example.notesappmvp.data.data_source.web;

import android.content.Context;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.notesappmvp.data.data_source.local.NotesLocalDataSource;
import com.example.notesappmvp.data.data_source.local.NotesLocalDataSourceImpl;
import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class FirebaseDataSourceImpl implements FirebaseDataSource {
    private CollectionReference notesRef;
    private FirebaseUser currentUser;
    private final NotesLocalDataSource localDataSource;

    public FirebaseDataSourceImpl(NotesLocalDataSource localDataSource) {
        this.localDataSource = localDataSource;
    }

    @Override
    public void setCurrentUser(FirebaseUser user) {
        if (user == null || user.getEmail() == null)
            return;
        currentUser = user;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        notesRef = firebaseFirestore.collection("users")
                .document(user.getEmail())
                .collection("notes");
    }

    @Override
    public void newLogin(FirebaseUser user) {
        setCurrentUser(user);

        new Thread() {
            @Override
            public void run() {
                syncNotes();
            }
        }.start();
    }

    @Override
    public void syncNotes() {
        Log.d("TAG", "syncNotes: Started...");

        //Unsaved Notes
        List<Note> unsavedNotes = localDataSource.synced_getAllNotes();

        //Downloading...
        Log.d("TAG", "syncNotes: Downloading...");
        notesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            Log.d("TAG", "syncNotes: Downloaded -> " + queryDocumentSnapshots.size());

            //Saving Downloaded notes
            for (DocumentSnapshot d : queryDocumentSnapshots) {
                Note note = convertToNote(d);
                if (note != null)
                    localDataSource.saveNote(note, null);
            }

            //Uploading Unsaved Notes
            if (unsavedNotes != null)
                uploadNotes(unsavedNotes);
        });
    }

    private void uploadNotes(List<Note> notes) {
        Log.d("TAG", "uploadNotes: Started -> " + notes.size());
        for (Note n : notes) {
            if (!isNoteAlreadyUploaded(n)) {
                saveNote(n);
            }
        }
    }

    private Note convertToNote(DocumentSnapshot documentSnapshot) {
        Note note = null;
        Map<String, Object> data = documentSnapshot.getData();
        if (data == null) return null;

        try {
            note = new Note(documentSnapshot.getId(),
                    data.get("title").toString(),
                    data.get("date").toString(),
                    data.get("msg").toString());
        } catch (Exception e) {
            return null;
        }
        return note;
    }

    @Override
    public void saveNote(Note note) {
        if (currentUser != null) {
            if (!isNoteAlreadyUploaded(note)) {
                Map<String, Object> hashedData = convertNoteToHashMap(note);
                notesRef.add(hashedData).addOnSuccessListener(documentReference -> {
                    note.setFirebase_id(documentReference.getId());
                    localDataSource.saveNote(note, null);
                    Log.d("TAG", "saveNote: Note Uploaded!");
                });
            } else {
                updateNote(note);
            }
        }
    }

    private boolean isNoteAlreadyUploaded(Note note) {
        return note.getFirebase_id() != null && !note.getFirebase_id().isEmpty();
    }

    private void updateNote(Note note) {
        if (currentUser != null && isNoteAlreadyUploaded(note)) {
            Map<String, Object> hashedData = convertNoteToHashMap(note);
            notesRef.document(note.getFirebase_id()).update(hashedData).addOnSuccessListener(
                    unused -> Log.d("TAG", "Note Updated!"));
        }
    }

    @Override
    public void deleteNote(Note note) {
        if (currentUser != null && isNoteAlreadyUploaded(note)) {
            notesRef.document(note.getFirebase_id()).delete().addOnSuccessListener(unused ->
                    Log.d("TAG", "onComplete: Note Deleted!"));
        }else {
            Log.d("TAG", "deleteNote: Can't delete: firebaseId: "+note.getFirebase_id());
        }
    }

    private Map<String, Object> convertNoteToHashMap(Note note) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("title", note.getTitle());
        noteData.put("msg", note.getMsg());
        noteData.put("date", note.getDate());
        return noteData;
    }

}
