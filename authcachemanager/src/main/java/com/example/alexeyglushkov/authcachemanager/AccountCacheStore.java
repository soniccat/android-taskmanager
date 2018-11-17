package com.example.alexeyglushkov.authcachemanager;

import androidx.annotation.Nullable;

import com.example.alexeyglushkov.authorization.Auth.Account;
import com.example.alexeyglushkov.authorization.Auth.AccountStore;
import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 07.11.15.
 */
public class AccountCacheStore extends DiskStorage implements AccountStore {

    private boolean isLoaded = false;
    List<Account> accounts = new ArrayList<>();

    public AccountCacheStore(File directory) {
        super(directory);
    }

    @Override
    public void putAccount(Account account) throws Exception {
        put(Integer.toString(account.getId()), account, null);
        accounts.add(account);
    }

    @Override
    @Nullable
    public Account getAccount(int key) {
        return (Account)getValue(Integer.toString(key));
    }

    @Override
    public int getAccountCount() {
        return getEntryCount();
    }

    @Override
    public int getMaxAccountId() {
        int maxId = 0;
        for (Account acc : accounts) {
            if (maxId < acc.getId()) {
                maxId = acc.getId();
            }
        }

        return maxId;
    }

    @Override
    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Account> getAccounts(int serviceType) {
        List<Account> filteredAccounts = new ArrayList<>();
        for (Account acc : accounts) {
            if (acc.getServiceType() == serviceType) {
                filteredAccounts.add(acc);
            }
        }

        return filteredAccounts;
    }

    @Override
    public void removeAccount(int id) throws Exception {
        remove(Integer.toString(id));
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void load() {
        List<StorageEntry> entries = getEntries();
        List<Account> accounts = new ArrayList<>();

        for (StorageEntry entry : entries) {
            try {
                Account account = (Account)entry.getObject();
                account.setAuthCredentialStore(this);
                accounts.add(account);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.accounts = accounts;
        isLoaded = true;
    }
}
