package com.example.notesappmvp.ui.dialogs;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.notesappmvp.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.progress_dialog);
    }
}
