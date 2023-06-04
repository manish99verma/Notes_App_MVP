package com.example.notesappmvp.ui.di;

import android.content.Context;

import com.example.notesappmvp.Preseter.main.MainActivityPresenter;
import com.example.notesappmvp.data.repository.NotesRepository;
import com.example.notesappmvp.ui.adapter.NotesAdapter;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;

@Module
@InstallIn(ActivityComponent.class)
public class MainActivityModule {

    @ActivityScoped
    @Provides
    MainActivityPresenter provideMainActivityPresenter(NotesRepository repository) {
        return new MainActivityPresenter(repository);
    }

    @ActivityScoped
    @Provides
    NotesAdapter provideNotesAdapter(@ActivityContext Context context) {
        return new NotesAdapter(context);
    }

}
