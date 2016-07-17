package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public interface DropboxCommandProvider {
    void setApi(DropboxAPI<AndroidAuthSession> api);

    //TODO: make interfaces for commands
    UploadCommand getUploadCommand(String srcPath, String dstPath);
    DownloadCommand getDownloadCommand(String srcPath, String dstPath);
}
