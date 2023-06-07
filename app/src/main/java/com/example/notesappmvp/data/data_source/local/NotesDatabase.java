package com.example.notesappmvp.data.data_source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.notesappmvp.data.model.Note;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class}, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {
    public abstract NotesDao notesDao();

    private static volatile NotesDatabase studentRoomDatabase;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static NotesDatabase getDatabase(final Context context) {
        if (studentRoomDatabase == null) {
            synchronized (NotesDatabase.class) {
                if (studentRoomDatabase == null) {
                    studentRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    NotesDatabase.class, "student_database")
                            .build();
                }
            }
        }
        return studentRoomDatabase;
    }
}
