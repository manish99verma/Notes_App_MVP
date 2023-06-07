package com.example.notesappmvp.data.data_source.local;

import androidx.lifecycle.LiveData;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;

import java.util.List;

public class NotesLocalDataSourceImpl implements NotesLocalDataSource {
    NotesDao notesDao;

    public NotesLocalDataSourceImpl(NotesDao notesDao) {
        this.notesDao = notesDao;
    }

    @Override
    public void saveNote(Note note, NotesRepository.OnNoteSavedEvent event) {
        NotesDatabase.databaseWriteExecutor.execute(() ->
        {
            long result = notesDao.saveNote(note);
            if (event != null)
                event.onNoteSaved(result);
        });
    }

    @Override
    public void deleteNote(Note note) {
        NotesDatabase.databaseWriteExecutor.execute(() -> notesDao.deleteNote(note));
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notesDao.getAllNotes();
    }

    @Override
    public List<Note> synced_getAllNotes() {
        return notesDao.syncedGetAllNotes();
    }
}
