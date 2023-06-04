package com.example.notesappmvp.ui.di;

import com.example.notesappmvp.Preseter.editor.EditorActivityPresenter;
import com.example.notesappmvp.data.repository.NotesRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.scopes.ActivityScoped;

@InstallIn(ActivityComponent.class)
@Module
public class EditorActivityModule {
    @ActivityScoped
    @Provides
    EditorActivityPresenter providePresenter(NotesRepository repository) {
        return new EditorActivityPresenter(repository);
    }
}
