package com.example.alexeyglushkov.authorization.tools

import com.example.alexeyglushkov.authorization.OAuth.Token

/**
 * Created by alexeyglushkov on 15.11.15.
 */
interface TokenExtractor {
    fun extract(response: String?): Token?
}