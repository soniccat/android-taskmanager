package com.example.alexeyglushkov.authorization.Auth

/**
 * Created by alexeyglushkov on 31.10.15.
 */
interface AuthCredentials {
    val isValid: Boolean
    val expireTime: Long
    val isExpired: Boolean
}