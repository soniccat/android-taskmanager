package com.example.alexeyglushkov.authorization.Auth;

import java.util.List;

/**
 * Created by alexeyglushkov on 31.10.15.
 */
public interface AccountStore {
    Error putAccount(Account account);
    Account getAccount(int key);
    int getAccountCount();
    int getMaxAccountId();
    List<Account> getAccounts();
    Error removeAccount(int id);
    Error getError();

    // TODO: provide async loader
    boolean isLoaded();
    void load();
}