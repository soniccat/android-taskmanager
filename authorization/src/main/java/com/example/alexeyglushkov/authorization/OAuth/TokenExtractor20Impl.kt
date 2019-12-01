package com.example.alexeyglushkov.authorization.OAuth

import com.example.alexeyglushkov.authorization.tools.TokenExtractor
import org.junit.Assert
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * Default implementation of [AccessTokenExtractor]. Conforms to OAuth 2.0
 */
class TokenExtractor20Impl : TokenExtractor {
    private var customPattern: Pattern? = null

    constructor() {}
    constructor(tokenName: String) {
        customPattern = Pattern.compile("$tokenName=([^&]+)")
    }

    /**
     * {@inheritDoc}
     */
    override fun extract(response: String?): Token? {
        Assert.assertNotNull(response, "Response body is incorrect. Can't extract a token from an empty string")
        val pattern = if (customPattern == null) TOKEN_REGEX else customPattern!!
        val matcher = pattern.matcher(response)

        return if (matcher.find()) {
            var token: String?
            try {
                token = URLDecoder.decode(matcher.group(1), "UTF-8")
                Token(token, EMPTY_SECRET, response)
            } catch (ex: UnsupportedEncodingException) {
                null
            }
        } else {
            null
        }
    }

    companion object {
        private val TOKEN_REGEX = Pattern.compile("access_token=([^&]+)")
        private const val EMPTY_SECRET = ""
    }
}