package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.service.SimpleService;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxService extends SimpleService {
    private DropboxAPI<AndroidAuthSession> api;

    public DropboxService(DropboxAccount account) {
        setAccount(account);
        this.api = new DropboxAPI<AndroidAuthSession>(account.getSession());
    }


}
