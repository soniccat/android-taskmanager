package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.example.alexeyglushkov.authtaskmanager.IServiceTask;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class DropboxUploadCommand extends ServiceTask implements IServiceTask {
    private String srcPath;
    private String dstPath;

    DropboxHelper helper;
    //private DropboxAPI.UploadRequest request;

    public DropboxUploadCommand(DropboxAPI<?> api, String srcPath, String dstPath) {
        super();

        this.helper = new DropboxHelper(api);
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    @Override
    public void startTask() {
        try {
            // TODO: read whole bytes is needed to upload to show progress
            helper.uploadFileOrDirectory(this.srcPath, this.dstPath, getProgressListener());
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
        };

        getPrivate().triggerProgressListeners(info);
    }

    @Override
    public void cancelTask(Object info) {
        super.cancelTask(info);
        // TODO: add cancellation support
        //request.abort();
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
                DropboxUploadCommand.this.triggerProgressListeners(bytes, total);
            }
        };
    }

}
