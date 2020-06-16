package com.example.speechrecognition.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.speechrecognition.R;

public class RestartGameDialog extends DialogFragment {

    private RestartGameCallback callback;
    private int messageId;

    public RestartGameDialog(RestartGameCallback callback, int messageId) {
        this.callback = callback;
        this.messageId = messageId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getText(R.string.end_of_game).toString())
                .setMessage(getResources().getText(messageId).toString())
                .setPositiveButton(getResources().getText(R.string.ok).toString(),
                        (dialog, which) -> callback.restartGame());
        return builder.create();
    }

}
