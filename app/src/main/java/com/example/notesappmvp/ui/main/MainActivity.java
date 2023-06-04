package com.example.notesappmvp.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesappmvp.Preseter.main.MainActivityPresenter;
import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.databinding.ActivityMainBinding;
import com.example.notesappmvp.ui.adapter.NotesAdapter;
import com.example.notesappmvp.ui.editor.EditNoteActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    MainActivityPresenter presenter;
    @Inject
    NotesAdapter adapter;

    private ActivityMainBinding binding;
    private boolean firstDataReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter.getAllNotes().observe(this, notes -> {
            Log.d("TAG", "NotesReceived: Size: " + notes.size());
            setUpRecyclerView(notes);
        });

        binding.btnAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            startActivity(intent);
        });

        binding.searchBtn.setOnClickListener(v -> {
            binding.toolbarLayout.setVisibility(View.INVISIBLE);
            binding.searchView.setVisibility(View.VISIBLE);

            showKeyboard(binding.searchView);
            binding.searchView.requestFocus();
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void setUpRecyclerView(List<Note> notes) {
        if (notes.size() > 0) {
            binding.RVMainActivity.setVisibility(View.VISIBLE);
            binding.noItemsLayout.setVisibility(View.INVISIBLE);
        } else {
            binding.RVMainActivity.setVisibility(View.INVISIBLE);
            binding.noItemsLayout.setVisibility(View.VISIBLE);
        }

        if (firstDataReceived) {
            adapter.submitList(notes);
            return;
        }

        firstDataReceived = true;

        // Layout Manager
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.RVMainActivity.setLayoutManager(staggeredGridLayoutManager);

        // Click Events
        adapter.setClickEvent(note -> {
            Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
            intent.putExtra("saved_note", note);
            startActivity(intent);
        });

        // Setting Adapter to RecyclerView
        binding.RVMainActivity.setAdapter(adapter);
        adapter.submitList(notes);
    }

    private void showKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }
}