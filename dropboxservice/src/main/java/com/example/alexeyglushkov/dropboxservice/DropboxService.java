package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.cachemanager.StorageProvider;
import com.example.alexeyglushkov.service.SimpleService;

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
        DropboxUploadCommand cmd = commandProvider.getUploadCommand(srcPath, dstPath);
        cmd.setServiceCommandCallback(callback);
        runCommand(cmd, true, callback);
    }

    public void download(String srcPath, String dstPath, final ServiceCommand.CommandCallback callback) {
        DropboxDownloadCommand cmd = commandProvider.getDownloadCommand(srcPath, dstPath);
        cmd.setServiceCommandCallback(callback);
        runCommand(cmd, true, callback);
    }

    public void sync(String srcPath, String dstPath, final ServiceCommand.CommandCallback callback) {
        lastSyncDate = loadLastSyncDate();

        DropboxSyncCommand cmd = commandProvider.getSyncCommand(srcPath, dstPath, lastSyncDate);
        cmd.setServiceCommandCallback(new ServiceCommand.CommandCallback() {
            @Override
            public void onCompleted(Error error) {
                if (error == null) {
                    storage.put(LAST_SYNC_DATE, (new Date()).getTime(), null);
                }

                callback.onCompleted(error);
            }
        });

        runCommand(cmd, true, callback);
    }

    private long loadLastSyncDate() {
        Object loadedInfo = storage.getValue(LAST_SYNC_DATE);
        return loadedInfo == null ? 0 : (long)loadedInfo;
    }
}
