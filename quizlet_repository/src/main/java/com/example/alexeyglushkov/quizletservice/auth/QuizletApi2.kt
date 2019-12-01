package com.example.alexeyglushkov.quizletservice.auth

import com.example.alexeyglushkov.authorization.Api.DefaultApi20
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand
import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig
import com.example.alexeyglushkov.authorization.OAuth.OAuthConstants
import com.example.alexeyglushkov.authorization.OAuth.OAuthCredentials
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder
import com.example.alexeyglushkov.authorization.requestbuilder.Verb
import com.example.alexeyglushkov.tools.TimeTools
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by alexeyglushkov on 20.03.16.
 */
class QuizletApi2 : DefaultApi20() {
    override fun getAuthorizationUrl(config: OAuthConfig): String {
        val callback = getEncodedCallback(config)
        checkNotNull(config.callback)
        checkNotNull(config.apiKey)
        checkNotNull(config.apiSecret)

        val urlBuilder = StringBuilder("https://quizlet.com/authorize")
        urlBuilder.append(String.format(Locale.US, "?client_id=%s", config.apiKey))
        urlBuilder.append("&response_type=code")
        urlBuilder.append("&scope=read%20write_set")
        urlBuilder.append(String.format(Locale.US, "&state=%d", authUrlState))
        urlBuilder.append(String.format(Locale.US, "&redirect_uri=%s", callback))
        return urlBuilder.toString()
    }

    private val authUrlState: Int
        private get() = Random().nextInt() % 10000

    override fun fillAccessTokenConnectionBuilder(builder: HttpUrlConnectionBuilder, config: OAuthConfig, code: String) {
        builder.setUrl("https://api.quizlet.com/oauth/token")
                .setVerb(Verb.POST)
        builder.addBodyParameter("grant_type", "authorization_code")
        builder.addBodyParameter(OAuthConstants.CODE, code)
        builder.addBodyParameter(OAuthConstants.REDIRECT_URI, config.callback!!)
        builder.addHeader("Authorization", getAuthHeader(config.apiKey, config.apiSecret))
    }

    private fun getAuthHeader(clentId: String, secretKey: String): String {
        val stringToEncode = "$clentId:$secretKey"
        val encodedString: String = android.util.Base64.encodeToString(stringToEncode.toByteArray(), android.util.Base64.DEFAULT)
        return "Basic $encodedString"
    }

    override fun createCredentials(response: String): OAuthCredentials {
        var credentials: OAuthCredentials? = null
        try {
            credentials = parseResponse(response)
        } catch (ex: JSONException) {
        }
        return credentials!!
    }

    @Throws(JSONException::class)
    private fun parseResponse(response: String): OAuthCredentials {
        val credentials: OAuthCredentials
        credentials = OAuthCredentials()
        val jsonObject = JSONObject(response)
        credentials.accessToken = jsonObject.getString("access_token")
        credentials.userId = jsonObject.getString("user_id")
        val scope = jsonObject.getString("scope")
        credentials.scopes = scope.split(" ").toTypedArray()
        val expiresIn = jsonObject.getInt("expires_in")
        credentials.expireTime = TimeTools.currentTimeSeconds() + expiresIn
        return credentials
    }

    override fun signCommand(command: ServiceCommand<*>, credentials: OAuthCredentials) {
        val authString = "Bearer " + credentials.accessToken
        command.connectionBuilder.addHeader("Authorization", authString)
    }
}