package com.example.alexeyglushkov.authorization

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.alexeyglushkov.tools.CancelError
import io.reactivex.Single

/**
 * Created by alexeyglushkov on 24.10.15.
 */
class AuthorizationActivity : AppCompatActivity() /*implements OAuthWebClient*/ {
    private var webView: WebView? = null
    private var isHandled = false

    private val loadUrl: String?
        get() = intent?.extras?.getString(LOAD_URL)

    private val callbackUrl: String?
        get() = intent?.extras?.getString(CALLBACK_URL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)

        webView = findViewById<WebView>(R.id.web_view).apply {
            bindWebView(this)
        }

        loadUrl?.let {
            runOnUiThread { webView?.loadUrl(it) }
        }
    }

    private fun bindWebView(webView: WebView) {
        webView.getSettings().javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                callbackUrl?.let {
                    if (url.startsWith(it)) {
                        AuthActivityProxy.finish(url, null)
                        if (!isHandled) {
                            isHandled = true
                            finish()
                        }
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                val error = Error("AuthorizationActivity webView error $errorCode $description")
                if (!isHandled) {
                    AuthActivityProxy.finish(null, error)
                    isHandled = true
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        webView = null
    }

    override fun finish() {
        if (!isHandled) {
            AuthActivityProxy.finish(null, CancelError())
            isHandled = true
        }
        webView!!.webViewClient = null
        super.finish()
        if (AuthActivityProxy.getCurrentActivity() === this) {
            AuthActivityProxy.setCurrentActivity(null)
        }
    }

//    fun loadUrl(url: String?): String {
//        runOnUiThread { webView?.loadUrl(url) }
//        return AuthActivityProxy.authResult()
//    }

    companion object {
        private const val TAG = "AuthorizationActivity"
        const val LOAD_URL = "LOAD_URL"
        const val CALLBACK_URL = "CALLBACK_URL"
    }
}