package com.example.alexeyglushkov.authorization.Auth;

import java.util.List;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AccountStore {
    Error putAccount(Account account);
    Account getAccount(String key);
    int getAccountCount();
    int getMaxAccountId();
    List<Account> getAccounts();
    Error removeAccount(String id);
    Error getError();

    // TODO: provide async loader
    boolean isLoaded();
    void load(LoadCallback callback);

    interface LoadCallback {
        void onLoaded(Error error);
    }
}