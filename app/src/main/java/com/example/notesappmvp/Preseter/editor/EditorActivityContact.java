package com.example.notesappmvp.Preseter.editor;

import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.data.repository.NotesRepository;

public class EditorActivityContact {
    interface presenter {
        void saveNote(Note note, NotesRepository.OnNoteSavedEvent event);

        void deleteNote(Note note);
    }
}
