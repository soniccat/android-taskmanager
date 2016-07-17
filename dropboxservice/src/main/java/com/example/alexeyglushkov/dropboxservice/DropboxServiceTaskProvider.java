package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.ServiceTaskProvider;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;

import java.io.File;

/**
 * Created by alexeyglushkov on 16.07.16.
 */
public class DropboxServiceTaskProvider implements DropboxCommandProvider {
    private DropboxAPI<AndroidAuthSession> api;

    public void setApi(DropboxAPI<AndroidAuthSession> api) {
        this.api = api;
    }

    public UploadCommand getUploadCommand(String srcPath, String dstPath) {
        return new UploadCommand(api, srcPath, dstPath);
    }

    public DownloadCommand getDownloadCommand(String srcPath, String dstPath) {
        return new DownloadCommand(api, srcPath, dstPath);
    }
}
