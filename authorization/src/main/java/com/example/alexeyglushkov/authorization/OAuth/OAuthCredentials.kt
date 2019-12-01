package com.example.alexeyglushkov.authorization.OAuth

import com.example.alexeyglushkov.authorization.Auth.AuthCredentials
import com.example.alexeyglushkov.tools.TimeTools
import java.io.Serializable

/**
 * Created by alexeyglushkov on 31.10.15.
 */
class OAuthCredentials : AuthCredentials, Serializable {
    // Getters / Setters
    var accessToken: String? = null
    var requestToken: String? = null
    var refreshToken: String? = null
    var userId: String? = null
    var scopes = arrayOf<String>()

    override var expireTime: Long = 0

    // AuthCredentials implementation
    override val isValid: Boolean
        get() {
            return accessToken != null && !isExpired
        }

    override val isExpired: Boolean
        get() {
            return expireTime != 0L && TimeTools.currentTimeSeconds() > expireTime
        }

    companion object {
        private const val serialVersionUID = 165145426084589963L
    }
}