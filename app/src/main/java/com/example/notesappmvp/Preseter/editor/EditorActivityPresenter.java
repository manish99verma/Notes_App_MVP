package com.example.notesappmvp.Preseter.editor;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;

public class EditorActivityPresenter implements EditorActivityContact.presenter {
    private final NotesRepository notesRepository;

    public EditorActivityPresenter(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    @Override
    public void saveNote(Note note, NotesRepository.OnNoteSavedEvent event) {
        notesRepository.saveNote(note, event);
    }

    @Override
    public void deleteNote(Note note) {
        notesRepository.deleteNote(note);
    }
}
