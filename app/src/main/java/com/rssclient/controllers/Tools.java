package com.rssclient.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public class Tools {

    public static void showErrorMessage(Context context, String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(string);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }
}
