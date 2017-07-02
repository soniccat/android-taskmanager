package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

/**
 * Created by alexeyglushkov on 16.07.16.
 */
public class DropboxServiceTaskProvider implements DropboxCommandProvider {
    private DropboxAPI<AndroidAuthSession> api;

    public void setApi(DropboxAPI<AndroidAuthSession> api) {
        this.api = api;
    }

    public DropboxUploadCommand getUploadCommand(String srcPath, String dstPath) {
        return new DropboxUploadCommand(api, srcPath, dstPath);
    }

    public DropboxDownloadCommand getDownloadCommand(String srcPath, String dstPath) {
        return new DropboxDownloadCommand(api, srcPath, dstPath);
    }

    public DropboxSyncCommand getSyncCommand(String srcPath, String dstPath, long lastSyncDate, DropboxCommandProvider.SyncCallback callback) {
        DropboxSyncCommand cmd = new DropboxSyncCommand(api, srcPath, dstPath, lastSyncDate);
        cmd.setDropboxCallback(callback);

        return cmd;
    }
}
