package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommandRunner;
import com.example.alexeyglushkov.service.SimpleService;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxService extends SimpleService {
    private DropboxCommandProvider commandProvider;
    private DropboxAPI<AndroidAuthSession> api;

    public DropboxService(DropboxAccount account, DropboxCommandProvider commandProvider, ServiceCommandRunner commandRunner) {
        setAccount(account);
        this.commandProvider = commandProvider;
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
}
