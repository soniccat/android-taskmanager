package com.example.alexeyglushkov.authorization.OAuth

import org.junit.Assert
import java.io.Serializable

class Token @JvmOverloads constructor(token: String, secret: String, rawResponse: String? = null) : Serializable {
    val token: String
    val secret: String
    private val rawResponse: String?

    fun getRawResponse(): String {
        checkNotNull(rawResponse) { "This token object was not constructed by scribe and does not have a rawResponse" }
        return rawResponse
    }

    fun getParameter(parameter: String): String? {
        var value: String? = null
        for (str in getRawResponse().split("&").toTypedArray()) {
            if (str.startsWith("$parameter=")) {
                val part = str.split("=").toTypedArray()
                if (part.size > 1) {
                    value = part[1].trim { it <= ' ' }
                }
                break
            }
        }
        return value
    }

    override fun toString(): String {
        return String.format("Token[%s , %s]", token, secret)
    }

    /**
     * Returns true if the token is empty (token = "", secret = "")
     */
    val isEmpty: Boolean
        get() = "" == token && "" == secret

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as Token
        return token == that.token && secret == that.secret
    }

    override fun hashCode(): Int {
        return 31 * token.hashCode() + 31 * secret.hashCode()
    }

    companion object {
        private const val serialVersionUID = 715000866082812683L
        /**
         * Factory method that returns an empty token (token = "", secret = "").
         *
         * Useful for two legged OAuth.
         */
        fun empty(): Token {
            return Token("", "")
        }
    }

    /**
     * Default constructor
     *
     * @param token token value. Can't be null.
     * @param secret token secret. Can't be null.
     */
    init {
        Assert.assertNotNull(token)
        Assert.assertNotNull(secret)
        this.token = token
        this.secret = secret
        this.rawResponse = rawResponse
    }
}