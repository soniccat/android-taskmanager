package com.rssclient.vm

import android.app.AlertDialog
import android.content.Context

interface ErrorViewModelContract {
    data class Error(val title: String?,
                     val message: String?,
                     val positiveAction: ErrorAction,
                     val negativeAction: ErrorAction?): Event() {
    }

    data class ErrorAction(val name: String, val callback: () -> Unit)
}

fun Context.showErrorDialog(error: ErrorViewModelContract.Error) {
    val builder = AlertDialog.Builder(this)
            .setTitle(error.title)
            .setMessage(error.message)
            .setPositiveButton(error.positiveAction.name) { _, _ ->
                error.positiveAction.callback()
            }

    val negativeAction = error.negativeAction
    if (negativeAction != null) {
        builder.setNegativeButton(error.negativeAction.name) { _, _ ->
            error.negativeAction.callback()
        }
        builder.setOnCancelListener {
            error.negativeAction.callback()
        }
    }

    builder.show()
}