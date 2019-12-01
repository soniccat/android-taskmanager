package com.example.alexeyglushkov.authorization.tools

import com.example.alexeyglushkov.authorization.OAuth.Token
import org.json.JSONException
import org.json.JSONObject

class JsonTokenExtractor : TokenExtractor {
    override fun extract(response: String?): Token? {
        var result: Token? = null
        try {
            val jsonObject = JSONObject(response)
            result = Token(jsonObject.getString("access_token"), "", response)
        } catch (ex: JSONException) {
        }
        return result
    }
}