package com.example.alexeyglushkov.authorization.OAuth

/**
 * Parameter object that groups OAuth config values
 *
 * @author Pablo Fernandez
 */
data class OAuthConfig @JvmOverloads constructor(val apiKey: String,
                                            val apiSecret: String,
                                            val callback: String? = null,
                                            val signatureType: SignatureType? = null,
                                            val scope: String? = null) {
}