package com.example.notesappmvp.ui.editor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notesappmvp.Preseter.editor.EditorActivityPresenter;
import com.example.notesappmvp.R;
import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.databinding.ActivityEditNoteBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditNoteActivity extends AppCompatActivity {
    @Inject
    EditorActivityPresenter presenter;
    private ActivityEditNoteBinding binding;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        note = (Note) getIntent().getSerializableExtra("saved_note");
        if (note == null)
            note = new Note("", getCurrentDate(), "");
        else
            loadOldNote();

        binding.backBtn.setOnClickListener(v -> onBackPressed());

        binding.saveBtn.setOnClickListener(v -> saveNote());

        binding.deleteBtn.setOnClickListener(v -> ShowDeleteDialog());
    }

    private void ShowDeleteDialog() {
        new MaterialAlertDialogBuilder(EditNoteActivity.this, R.style.AlertDialogTheme)
                .setTitle("Delete")
                .setMessage("Do you really want to delete this note?")
                .setPositiveButton("Yes", (dialog, which) -> deleteNote())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteNote() {
        presenter.deleteNote(note);
        Toast.makeText(this, "Note Successfully deleted!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadOldNote() {
        binding.edtTitle.setText(note.getTitle());
        binding.edtMsg.setText(note.getMsg());
        binding.deleteBtn.setVisibility(View.VISIBLE);
    }

//    private void saveFakeNotes() {
//        List<Note> notes = new ArrayList<>();
//        notes.add(new Note("How to make your personal brand stand out online", getCurrentDate(), "This is just empty Now."));
//        notes.add(new Note("Beautiful weather app UI concepts we wish existed", getCurrentDate(), "This is just empty Now."));
//        notes.add(new Note("10 excellent font pairing tools for designers", getCurrentDate(), "This is just empty Now."));
//        notes.add(new Note("Spotify's Reema Bhagat on product design, music, and the key to a happy career", getCurrentDate(), "This is just empty Now."));
//        notes.add(new Note("12 eye-catching mobile wallpaper", getCurrentDate(), "This is just empty Now."));
//
//        for (Note note : notes) {
//            presenter.saveNote(note, id -> {
//
//            });
//        }
//    }

    private void saveNote() {
        hideKeyboard(EditNoteActivity.this);

        note.setTitle(binding.edtTitle.getText().toString());
        note.setMsg(binding.edtMsg.getText().toString());

        String validateNoteMsg = validateNote(note);
        if (validateNoteMsg.isEmpty()) {
            presenter.saveNote(note, id -> runOnUiThread(() -> {
                Log.d("TAG", "savedNote: "+id);
                String msg = "Note successfully saved!";
                if (id >= 0) {
                    binding.deleteBtn.setVisibility(View.VISIBLE);
                    note.setId((int) id);
                } else {
                    msg = "Failed to save note.";
                }
                Toast.makeText(EditNoteActivity.this, msg, Toast.LENGTH_SHORT).show();
            }));

        } else
            Toast.makeText(this, validateNoteMsg, Toast.LENGTH_SHORT).show();
    }

    private String getCurrentDate() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        return (months[currentMonth] + " " + currentDay + ", " + currentYear);
    }

    private String validateNote(Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty())
            return "Note's title can't be empty!";

        if (note.getMsg() == null || note.getMsg().isEmpty())
            return "Note's message can't be empty!";

        return "";
    }

    private void showDialogIfNotSaved() {
        new MaterialAlertDialogBuilder(EditNoteActivity.this, R.style.AlertDialogTheme)
                .setTitle("Are you sure?")
                .setMessage("Your note is not saved yet. Do you really want to exit.")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        if (!isEdited())
            super.onBackPressed();
        else
            showDialogIfNotSaved();
    }

    private boolean isEdited() {
        String title = binding.edtTitle.getText().toString();
        String msg = binding.edtMsg.getText().toString();
        boolean res = title.equals(note.getTitle()) && msg.equals(note.getMsg());
        return !res;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}