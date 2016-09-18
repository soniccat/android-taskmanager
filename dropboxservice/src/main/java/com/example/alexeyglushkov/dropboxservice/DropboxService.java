package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.service.SimpleService;

import java.io.File;
import java.util.Date;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxService extends SimpleService {
    private static final String LAST_SYNC_DATE = "LAST_SYNC_DATE";

    private DropboxCommandProvider commandProvider;
    private DropboxAPI<AndroidAuthSession> api;
    private StorageProvider storage;

    private long lastSyncDate = 0;
    private Callback callback;

    public DropboxService(DropboxAccount account, DropboxCommandProvider commandProvider, ServiceCommandRunner commandRunner, StorageProvider storage) {
        setAccount(account);
        this.commandProvider = commandProvider;
        this.storage = storage;
        setServiceCommandRunner(commandRunner);

        this.api = new DropboxAPI<AndroidAuthSession>(account.getSession());
        this.commandProvider.setApi(this.api);
    }

    public void upload(String srcPath, String dstPath, final ServiceCommand.CommandCallback callback) {
        //TODO: handle 401 error
        ServiceCommand cmd = commandProvider.getUploadCommand(srcPath, dstPath);
        cmd.setServiceCommandCallback(callback);
        runCommand(cmd, true, callback);
    }

    public void download(String srcPath, String dstPath, final ServiceCommand.CommandCallback callback) {
        ServiceCommand cmd = commandProvider.getDownloadCommand(srcPath, dstPath);
        cmd.setServiceCommandCallback(callback);
        runCommand(cmd, true, callback);
    }

    public void sync(String srcPath, String dstPath, final ServiceCommand.CommandCallback callback) {
        lastSyncDate = loadLastSyncDate();

        DropboxSyncCommand cmd = commandProvider.getSyncCommand(srcPath, dstPath, lastSyncDate, getSyncCallback());
        cmd.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                if (error == null) {
                    try {
                        storage.put(LAST_SYNC_DATE, (new Date()).getTime(), null);
                    } catch (Exception e) {
                    }
                }

                callback.onCompleted(error);
            }
        });

        runCommand(cmd, true, callback);
    }

    private @Nullable DropboxCommandProvider.SyncCallback getSyncCallback() {
        DropboxCommandProvider.SyncCallback result = new DropboxCommandProvider.SyncCallback() {
            @Override
            public void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxFile, DropboxCommandProvider.MergeCompletion completion) {
                callback.merge(localFile, dropboxFile, completion);
            }
        };

        return callback != null ? result : null;
    }

    private long loadLastSyncDate() {
        Object loadedInfo = storage.getValue(LAST_SYNC_DATE);
        return loadedInfo == null ? 0 : (long)loadedInfo;
    }

    //// Inner Interfaces

    public interface Callback {
        void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxFile, DropboxCommandProvider.MergeCompletion completion);
    }

    //// Setter

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    //// Getter

    public DropboxAPI<AndroidAuthSession> getApi() {
        return api;
    }
}
