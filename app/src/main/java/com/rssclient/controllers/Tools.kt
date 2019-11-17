package com.rssclient.controllers

import android.app.AlertDialog.Builder
import android.content.Context

object Tools {
    fun showErrorMessage(context: Context?, string: String?) {
        val builder = Builder(context)
        builder.setTitle(string)
        builder.setPositiveButton("Ok", null)
        builder.create().show()
    }
}