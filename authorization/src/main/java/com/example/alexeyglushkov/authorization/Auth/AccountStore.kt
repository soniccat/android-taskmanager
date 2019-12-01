package com.example.alexeyglushkov.authorization.Auth

/**
 * Created by alexeyglushkov on 31.10.15.
 */
interface AccountStore {
    @Throws(Exception::class)
    fun putAccount(account: Account?)

    @Throws(Exception::class)
    fun getAccount(key: Int): Account?

    val accountCount: Int
    val maxAccountId: Int
    val accounts: List<Account?>?
    fun getAccounts(serviceType: Int): List<Account?>?
    @Throws(Exception::class)
    fun removeAccount(id: Int)

    @Throws(Exception::class)
    fun removeAll()

    // TODO: provide async loader
    val isLoaded: Boolean

    @Throws(Exception::class)
    fun load()
}