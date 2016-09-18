package com.example.alexeyglushkov.authorization.Auth;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AccountStore {
    void putAccount(Account account) throws Exception;

    @Nullable
    Account getAccount(int key);
    int getAccountCount();
    int getMaxAccountId();

    List<Account> getAccounts();
    List<Account> getAccounts(int serviceType);
    Error removeAccount(int id);
    Error removeAll();
    Error getError();

    // TODO: provide async loader
    boolean isLoaded();
    void load();
}