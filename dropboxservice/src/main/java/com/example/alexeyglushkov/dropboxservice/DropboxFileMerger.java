package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.NonNull;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class DropboxFileMerger {
    private DropboxAPI<AndroidAuthSession> api;
    private File tmpDir;
    private FileMerger fileMerger;

    public DropboxFileMerger(DropboxAPI<AndroidAuthSession> api, File tmpDir, FileMerger fileMerger) {
        this.api = api;
        this.tmpDir = tmpDir;
        this.fileMerger = fileMerger;
    }

    public void merge(@NonNull final File localFile, @NonNull DropboxAPI.Entry dropboxEntry, final DropboxSyncCommand.MergeCompletion completion) throws Exception {
        UUID dropboxFileName = UUID.randomUUID();
        final File dropboxFile = File.createTempFile(dropboxFileName.toString(), "", tmpDir);

        DropboxHelper helper = new DropboxHelper(api);
        helper.downloadFile(dropboxEntry.path, dropboxFile.getPath(), null);

        File result = fileMerger.merge(localFile, dropboxFile);
        completion.completed(result, null);
    }
}
