package com.example.notesappmvp.ui.di;

import com.example.notesappmvp.data.data_source.NotesLocalDataSource;
import com.example.notesappmvp.data.repository.NotesRepository;
import com.example.notesappmvp.data.repository.NotesRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {
    @Provides
    @Singleton
    NotesRepository provideNotesRepository(NotesLocalDataSource notesLocalDataSource) {
        return new NotesRepositoryImpl(notesLocalDataSource);
    }

}
