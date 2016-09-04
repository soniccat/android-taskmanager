package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public interface DropboxCommandProvider {
    void setApi(DropboxAPI<AndroidAuthSession> api);

    //TODO: make interfaces for commands or return ServiceTask
    DropboxUploadCommand getUploadCommand(String srcPath, String dstPath);
    DropboxDownloadCommand getDownloadCommand(String srcPath, String dstPath);
    DropboxSyncCommand getSyncCommand(String srcPath, String dstPath, long lastSyncDate);
}
