package com.example.alexeyglushkov.wordteacher.main

import androidx.appcompat.app.AppCompatActivity

import com.example.alexeyglushkov.authorization.AuthActivityProxy

/**
 * Created by alexeyglushkov on 12.12.15.
 */
open class BaseActivity : AppCompatActivity() {
    override fun onResume() {
        super.onResume()
        AuthActivityProxy.setCurrentActivity(this)

        // finish dropbox auth
        //        AccountStore store = MainApplication.instance.getAccountStore();
        //        if (store != null) {
        //            List<Account> accounts = store.getAccounts(Networks.Network.Dropbox.ordinal());
        //            if (accounts.size() > 0) {
        //                DropboxAccount dropboxAccount = (DropboxAccount) accounts.get(0);
        //                dropboxAccount.onResume();
        //            }
        //        }
    }
}
