package com.example.alexeyglushkov.authorization.Api

import com.example.alexeyglushkov.authorization.OAuth.OAuthConfig

/**
 * Created by alexeyglushkov on 04.11.15.
 */
interface OAuthApi : Api {
    var config: OAuthConfig
}