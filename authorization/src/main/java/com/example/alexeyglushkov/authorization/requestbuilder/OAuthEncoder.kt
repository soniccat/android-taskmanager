package com.example.alexeyglushkov.authorization.requestbuilder

import org.junit.Assert
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

/**
 * @author Pablo Fernandez
 */
object OAuthEncoder {
    private const val CHARSET = "UTF-8"
    private var ENCODING_RULES: Map<String, String>? = null

    fun encode(plain: String?): String? {
        Assert.assertNotNull(plain, "Cannot encode null object")
        var encoded = ""
        encoded = try {
            URLEncoder.encode(plain, CHARSET)
        } catch (uee: UnsupportedEncodingException) {
            return null
        }
        for ((key, value) in ENCODING_RULES!!) {
            encoded = applyRule(encoded, key, value)
        }
        return encoded
    }

    private fun applyRule(encoded: String, toReplace: String, replacement: String): String {
        return encoded.replace(Pattern.quote(toReplace).toRegex(), replacement)
    }

    fun decode(encoded: String?): String? {
        Assert.assertNotNull(encoded, "Cannot decode null object")
        return try {
            URLDecoder.decode(encoded, CHARSET)
        } catch (uee: UnsupportedEncodingException) {
            null
        }
    }

    init {
        val rules = HashMap<String, String>()
        rules["*"] = "%2A"
        rules["+"] = "%20"
        rules["%7E"] = "~"
        ENCODING_RULES = Collections.unmodifiableMap(rules)
    }
}