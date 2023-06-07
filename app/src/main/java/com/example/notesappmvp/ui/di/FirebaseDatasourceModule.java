package com.example.notesappmvp.ui.di;

import com.example.notesappmvp.data.data_source.local.NotesLocalDataSource;
import com.example.notesappmvp.data.data_source.web.FirebaseDataSource;
import com.example.notesappmvp.data.data_source.web.FirebaseDataSourceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class FirebaseDatasourceModule {
    @Provides
    @Singleton
    FirebaseDataSource provideFirebaseDataSourceModule(NotesLocalDataSource localDataSource) {
        return new FirebaseDataSourceImpl(localDataSource);
    }
}
