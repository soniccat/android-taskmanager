package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.NonNull;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public interface DropboxCommandProvider {
    void setApi(DropboxAPI<AndroidAuthSession> api);

    //TODO: make interfaces for commands or return ServiceTask
    ServiceCommand getUploadCommand(String srcPath, String dstPath);
    ServiceCommand getDownloadCommand(String srcPath, String dstPath);
    DropboxSyncCommand getSyncCommand(String srcPath, String dstPath, long lastSyncDate, DropboxCommandProvider.SyncCallback callback);

    //// Inner Interfaces

    interface SyncCallback {
        void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxFile, MergeCompletion completion);
    }

    interface MergeCompletion {
        void completed(File result, Error error);
    }
}
