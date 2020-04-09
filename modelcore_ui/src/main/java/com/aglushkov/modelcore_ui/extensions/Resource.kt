package com.aglushkov.modelcore_ui.extensions

import android.content.Context
import com.aglushkov.modelcore.resource.Resource
import com.aglushkov.modelcore_ui.R

fun Resource<*>?.getErrorString(context: Context, hasConnection: Boolean, hasResponse: Boolean): String? {
    if (this is Resource.Error) {
        if (!hasConnection) {
            return context.getString(R.string.error_no_connection)
        } else if (!hasResponse) {
            return context.getString(R.string.error_bad_connection)
        } else {
            return context.getString(R.string.error_bad_response)
        }
    }

    return null
}