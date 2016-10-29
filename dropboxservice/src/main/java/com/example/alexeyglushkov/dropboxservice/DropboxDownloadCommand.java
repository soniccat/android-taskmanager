package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by alexeyglushkov on 17.07.16.
 */
public class DropboxDownloadCommand extends ServiceTask {
    private String srcPath;
    private String destPath;

    private DropboxHelper helper;

    public DropboxDownloadCommand(DropboxAPI<?> api, String srcPath, String destPath) {
        this.helper = new DropboxHelper(api);
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    @Override
    public void startTask() {
        try {
            helper.downloadFileOrDir(srcPath, destPath, getProgressListener());
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPrivate().handleTaskCompletion();
    }

    private void triggerProgressListeners(final long bytes, final long total) {
        ProgressInfo info = new ProgressInfo() {
            @Override
            public float getCurrentValue() {
                return bytes;
            }

            @Override
            public float getNormalizedValue() {
                return (float)bytes/(float)total;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        };

        getPrivate().triggerProgressListeners(info);
    }

    //// Creation Method

    private com.dropbox.client2.ProgressListener getProgressListener() {
        return new com.dropbox.client2.ProgressListener() {
            @Override
            public long progressInterval() {
                // Update the progress bar every half-second or so
                return 500;
            }

            @Override
            public void onProgress(long bytes, long total) {
                DropboxDownloadCommand.this.triggerProgressListeners(bytes, total);
            }
        };
    }
}
