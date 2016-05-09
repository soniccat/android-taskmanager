package com.example.alexeyglushkov.wordteacher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class RenameAlert {

    private TextInputLayout inputLayout;
    private View.OnClickListener positiveButtonListener;

    public String getName() {
        return inputLayout.getEditText().getText().toString();
    }

    public void setPositiveButtonListener(View.OnClickListener positiveButtonListener) {
        this.positiveButtonListener = positiveButtonListener;
    }

    public void show(final Context context, ViewGroup viewGroup) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View titleView = LayoutInflater.from(context).inflate(R.layout.dialog_rename, viewGroup, false);
        inputLayout = (TextInputLayout)titleView.findViewById(R.id.input_layout);
        inputLayout.setError("");

        builder.setTitle(R.string.dialog_rename_hint);
        builder.setView(titleView);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getName().length() == 0) {
                    String str = context.getString(R.string.error_empty_title);
                    inputLayout.setError(str);
                } else {
                    if (positiveButtonListener != null) {
                        positiveButtonListener.onClick(v);
                    }

                    alertDialog.dismiss();
                }
            }
        });
    }
}
