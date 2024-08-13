package com.example.simulascore;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ErrorConnectionDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    public static ErrorConnectionDialogFragment newInstance(String message) {
        ErrorConnectionDialogFragment fragment = new ErrorConnectionDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(ARG_MESSAGE);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Error de Conexión")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
    }
}
