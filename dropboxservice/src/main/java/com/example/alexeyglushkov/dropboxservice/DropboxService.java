package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.service.SimpleService;

import java.io.File;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxService extends SimpleService {
    private DropboxCommandProvider commandProvider;
    private DropboxAPI<AndroidAuthSession> api;
    private String appPath;

    public DropboxService(String appPath, DropboxAccount account, DropboxCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        this.appPath = appPath;
        setAccount(account);
        this.commandProvider = commandProvider;
        setServiceCommandRunner(commandRunner);

        this.api = new DropboxAPI<AndroidAuthSession>(account.getSession());
        this.commandProvider.setApi(this.api);
    }

    public void uploadFile(File file, final ServiceCommand.CommandCallback callback) {
        UploadCommand cmd = commandProvider.getUploadCommand(appPath, file);
        cmd.setServiceCommandCallback(callback);
        runCommand(cmd, true, callback);
    }
}
