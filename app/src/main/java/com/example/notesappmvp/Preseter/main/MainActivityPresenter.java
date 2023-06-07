package com.example.notesappmvp.Preseter.main;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    public LiveData<List<Note>> filterNotes(String query, List<Note> list) {
        MutableLiveData<List<Note>> liveData = new MutableLiveData<>();

        new Thread() {
            @Override
            public void run() {

                if (query == null || query.isEmpty()) {
                    new Handler(Looper.getMainLooper()).post(() -> liveData.setValue(list));
                    return;
                }

                String lowerCaseQuery = query.toLowerCase();
                List<Note> newList = new LinkedList<>();
                for (Note note : list) {
                    if (note.getDate().toLowerCase().contains(lowerCaseQuery)
                            || note.getTitle().toLowerCase().contains(lowerCaseQuery)
                            || note.getMsg().toLowerCase().contains(lowerCaseQuery))
                        newList.add(note);
                }

                new Handler(Looper.getMainLooper()).post(() -> liveData.setValue(newList));
            }
        }.start();


        return liveData;
    }

    @Override
    public void setNewUser(FirebaseUser user) {
        notesRepository.setCurrentUser(user);
    }

    @Override
    public void newLogin(FirebaseUser user) {
        notesRepository.newLogin(user);
    }
}
