package com.example.notesappmvp.data.data_source.local;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;

import java.util.List;

public interface NotesLocalDataSource {
    void saveNote(Note note, NotesRepository.OnNoteSavedEvent event);

    void deleteNote(Note note);

    LiveData<List<Note>> getAllNotes();

    List<Note> synced_getAllNotes();
}
