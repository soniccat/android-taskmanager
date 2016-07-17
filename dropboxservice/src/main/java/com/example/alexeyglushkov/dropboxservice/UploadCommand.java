package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.IServiceTask;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.streamlib.progress.ProgressUpdater;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public class UploadCommand extends ServiceTask implements IServiceTask {
    private DropboxAPI<?> api;
    private String srcPath;
    private String dstPath;

    private DropboxAPI.UploadRequest request;

    public UploadCommand(DropboxAPI<?> api, String srcPath, String dstPath) {
        super();

        this.api = api;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    @Override
    public void startTask() {
        // TODO: read whole bytes needed to upload to show progress
        uploadFileOrDirectory(this.srcPath, this.dstPath);
        getPrivate().handleTaskCompletion();
    }

    private void uploadFileOrDirectory(String srcPath, String dstPath) {
        File srcFile = new File(srcPath);
        if (srcFile.isDirectory()) {
            String resultDstPath = addPathName(dstPath, srcFile.getName());
            File[] files = srcFile.listFiles();
            for (File f : files) {
                uploadFileOrDirectory(f.getPath(), addPathName(resultDstPath, f.getName()));
            }
        } else {
            uploadFile(srcPath, dstPath);
        }
    }

    private String addPathName(String path, String name) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        path = path + name;
        return path;
    }

    private void uploadFile(String srcPath, String dstPath) {
        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            File file = new File(srcPath);
            FileInputStream fis = new FileInputStream(file);

            request = api.putFileOverwriteRequest(dstPath, fis, file.length(),
                    new com.dropbox.client2.ProgressListener() {
                        @Override
                        public long progressInterval() {
                            // Update the progress bar every half-second or so
                            return 500;
                        }

                        @Override
                        public void onProgress(long bytes, long total) {
                            UploadCommand.this.triggerProgressListeners(bytes, total);
                        }
                    });

            if (request != null) {
                request.upload();
            }

        } catch (Exception e) {
            e.printStackTrace();
            getPrivate().setTaskError(new Error(e));
        }
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
        request.abort();
    }
}
