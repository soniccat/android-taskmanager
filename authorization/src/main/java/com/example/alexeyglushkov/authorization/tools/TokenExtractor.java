package com.example.alexeyglushkov.authorization.tools;

import com.example.alexeyglushkov.authorization.OAuth.Token;

/**
 * Created by alexeyglushkov on 15.11.15.
 */
public interface TokenExtractor {
    Token extract(String response);
}
