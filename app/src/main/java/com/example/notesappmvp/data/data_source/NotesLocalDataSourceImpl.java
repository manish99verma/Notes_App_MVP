package com.example.notesappmvp.data.data_source;

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
        NotesDatabase.databaseWriteExecutor.execute(() -> event.onNoteSaved(notesDao.saveNote(note)));
    }

    @Override
    public void deleteNote(Note note) {
        NotesDatabase.databaseWriteExecutor.execute(() -> notesDao.deleteNote(note));
    }

    @Override
    public LiveData<List<Note>> getAllNotes() {
        return notesDao.getAllNotes();
    }
}
