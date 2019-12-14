package com.example.alexeyglushkov.authorization

import android.app.Activity
import android.content.Intent
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import java.lang.ref.WeakReference

/**
 * Created by alexeyglushkov on 25.11.15.
 */
class AuthActivityProxy : OAuthWebClient {
    override suspend fun loadUrl(url: String, callback: String): String {
        Assert.assertNotNull(currentActivity)
        val intent = Intent(getCurrentActivity(), AuthorizationActivity::class.java)
        intent.putExtra(AuthorizationActivity.LOAD_URL, url)
        intent.putExtra(AuthorizationActivity.CALLBACK_URL, callback)

        authResult = Channel()
        getCurrentActivity()?.startActivity(intent)
        return authResult.receive()
    }

    companion object {
        private var currentActivity: WeakReference<Activity>? = null
        var authResult = Channel<String>()

        //private static Callback currentCallback;
        fun getCurrentActivity(): Activity? {
            return if (currentActivity != null) currentActivity?.get() else null
        }

        fun setCurrentActivity(currentActivity: Activity?) {
            Companion.currentActivity = if (currentActivity != null) WeakReference(currentActivity) else null
        }

        fun finish(url: String?, error: Error?) = runBlocking {
            if (error != null || url == null) {
                val resultError = if (error != null) error else IllegalArgumentException(url)
                authResult.close(resultError)
            } else {
                authResult.send(url)
                authResult.close(null)
            }
        }
    }
}