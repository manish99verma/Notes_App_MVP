package com.example.notesappmvp.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.notesappmvp.Preseter.main.MainActivityPresenter;
import com.example.notesappmvp.R;
import com.example.notesappmvp.data.model.Note;
import com.example.notesappmvp.databinding.ActivityMainBinding;
import com.example.notesappmvp.ui.adapter.NotesAdapter;
import com.example.notesappmvp.ui.dialogs.ProgressDialog;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    MainActivityPresenter presenter;
    @Inject
    NotesAdapter adapter;
    private ActivityMainBinding binding;
    private boolean firstDataReceived = false;// First data from database
    private List<Note> currentList;// List of all notes with filtering
    private boolean isScreenReady = false;//Will remove splash screen
    private ProgressDialog loadingDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        //Keep Splash Screen
        View content = findViewById(android.R.id.content);
        //Add the pre draw listener to the view tree observer
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isScreenReady) {
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }

                return false;
            }
        });

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter.getAllNotes().observe(this, notes -> {
            Log.d("TAG", "NotesReceived: Size: " + notes.size());
            currentList = notes;
            setUpRecyclerView(notes, false);
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
                filterNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                new Handler().postDelayed(() -> filterNotes(newText), 1000);
                return false;
            }
        });

        //Login
        loadingDialog = new ProgressDialog(MainActivity.this);
        loadingDialog.setCancelable(false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser);
        binding.loginBtn.setOnClickListener(v -> startLogin());
    }

    private void startLogin() {
        loadingDialog.show();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        someActivityResultLauncher.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    loadingDialog.dismiss();

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                            firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Log.w("TAG", "Google sign in failed", e);
                        }

                    }
                }
            });

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        loadingDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    loadingDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        presenter.newLogin(user);
                        binding.loginBtn.setVisibility(View.INVISIBLE);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser o) {
        if (o == null)
            Log.i("TAG", "updateUI: NUll");
        else {
            binding.loginBtn.setVisibility(View.INVISIBLE);
            presenter.setNewUser(o);
            Log.d("TAG", "updateUI: " + o.getEmail());
        }
    }

    private void filterNotes(String query) {
        if (currentList.size() < 1)
            return;
        presenter.filterNotes(query, currentList).observe(MainActivity.this, notes -> setUpRecyclerView(notes, true));
    }

    private void setUpRecyclerView(List<Note> notes, boolean isSearching) {
        if (notes.size() > 0) {
            binding.RVMainActivity.setVisibility(View.VISIBLE);
            binding.noItemsLayout.setVisibility(View.INVISIBLE);
        } else {
            if (isSearching)
                binding.txtNotFound.setText(R.string.msg_no_matches);
            else
                binding.txtNotFound.setText(R.string.msg_no_notes);

            binding.RVMainActivity.setVisibility(View.INVISIBLE);
            binding.noItemsLayout.setVisibility(View.VISIBLE);
        }

        if (firstDataReceived)
            adapter.submitList(notes);
        else {
            firstDataReceived = true;

            // Layout Manager
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            binding.RVMainActivity.setLayoutManager(staggeredGridLayoutManager);

            // Click Events
            adapter.setClickEvent(note -> {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                intent.putExtra("saved_note", note);
                binding.searchView.clearFocus();
                startActivity(intent);
            });

            // Setting Adapter to RecyclerView
            binding.RVMainActivity.setAdapter(adapter);
            adapter.submitList(notes);
        }

        new Handler().postDelayed(() -> isScreenReady = true, 1000);
    }

    private void showKeyboard(View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onBackPressed() {
        Log.i("TAG", "onBackPressed: " + (binding.searchView.getVisibility() == View.VISIBLE));

        if (binding.searchView.getVisibility() == View.VISIBLE) {
            binding.searchView.clearFocus();
            binding.searchView.setQuery("", false);
            binding.searchView.setVisibility(View.INVISIBLE);
            binding.toolbarLayout.setVisibility(View.VISIBLE);
            setUpRecyclerView(currentList, false);
        } else
            super.onBackPressed();
    }
}