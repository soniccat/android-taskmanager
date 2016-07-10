package main;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.dropboxservice.DropboxAccount;

import java.util.List;

import authorization.AuthActivityProxy;

/**
 * Created by alexeyglushkov on 12.12.15.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        super.onResume();
        AuthActivityProxy.setCurrentActivity(this);

        // finish dropbox auth
        AccountStore store = MainApplication.instance.getAccountStore();
        if (store != null) {
            List<Account> accounts = store.getAccounts(Networks.Network.Dropbox.ordinal());
            if (accounts.size() > 0) {
                DropboxAccount dropboxAccount = (DropboxAccount) accounts.get(0);
                dropboxAccount.onResume();
            }
        }
    }
}
