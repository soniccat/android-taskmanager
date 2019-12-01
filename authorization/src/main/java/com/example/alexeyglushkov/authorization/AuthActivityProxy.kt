package com.example.alexeyglushkov.authorization

import android.app.Activity
import android.content.Intent
import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by alexeyglushkov on 25.11.15.
 */
class AuthActivityProxy : OAuthWebClient {
    override fun loadUrl(url: String, callback: String): Single<String?> {
        Assert.assertNotNull(currentActivity)
        val intent = Intent(getCurrentActivity(), AuthorizationActivity::class.java)
        intent.putExtra(AuthorizationActivity.LOAD_URL, url)
        intent.putExtra(AuthorizationActivity.CALLBACK_URL, callback)

        getCurrentActivity()?.startActivity(intent)
        return getSingleAuthResult()
    }

    companion object {
        private var currentActivity: WeakReference<Activity>? = null
        private var emitter: ObservableEmitter<String>? = null

        val authResult: Observable<String> = Observable.create<String>(object : ObservableOnSubscribe<String> {
            @Throws(Exception::class)
            override fun subscribe(emitter: ObservableEmitter<String>) {
                emitter.setCancellable {
                    if (Companion.emitter != emitter) {
                        Companion.emitter = null
                    }
                }
                Companion.emitter = emitter
            }
        })

        fun getSingleAuthResult() = Single.fromObservable<String>(authResult)

        //private static Callback currentCallback;
        fun getCurrentActivity(): Activity? {
            return if (currentActivity != null) currentActivity?.get() else null
        }

        fun setCurrentActivity(currentActivity: Activity?) {
            Companion.currentActivity = if (currentActivity != null) WeakReference(currentActivity) else null
        }

        fun finish(url: String?, error: Error?) {
            if (error != null || url == null) {
                val resultError = if (error != null) error else IllegalArgumentException(url)
                emitter?.onError(resultError)
            } else {
                emitter?.onNext(url)
                emitter?.onComplete()
            }
        }
    }
}