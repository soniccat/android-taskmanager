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
}
