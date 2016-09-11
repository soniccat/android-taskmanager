package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public interface DropboxCommandProvider {
    void setApi(DropboxAPI<AndroidAuthSession> api);

    //TODO: make interfaces for commands or return ServiceTask
    ServiceCommand getUploadCommand(String srcPath, String dstPath);
    ServiceCommand getDownloadCommand(String srcPath, String dstPath);
    DropboxSyncCommand getSyncCommand(String srcPath, String dstPath, long lastSyncDate, DropboxSyncCommand.SyncCallback callback);
}
