package com.example.notesappmvp.ui.di;

import android.app.Application;

import com.example.notesappmvp.data.data_source.NotesDatabase;
import com.example.notesappmvp.data.data_source.NotesLocalDataSource;
import com.example.notesappmvp.data.data_source.NotesLocalDataSourceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class LocalDataModule {
    @Provides
    @Singleton
    NotesDatabase provideNoteDatabase(Application app) {
        return NotesDatabase.getDatabase(app);
    }

    @Provides
    @Singleton
    NotesLocalDataSource provideNoteLocalDataSource(NotesDatabase notesDatabase) {
        return new NotesLocalDataSourceImpl(notesDatabase.notesDao());
    }

}
