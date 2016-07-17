package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authorization.requestbuilder.HttpUrlConnectionBuilder;
import com.example.alexeyglushkov.authtaskmanager.IServiceTask;
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
public class UploadCommand extends SimpleTask implements IServiceTask {
    private DropboxAPI<?> api;
    private String path;
    private File file;

    private DropboxAPI.UploadRequest request;

    public UploadCommand(DropboxAPI<?> api, String dropboxPath, File file) {
        super();

        this.api = api;
        this.path = dropboxPath;
        this.file = file;
    }

    @Override
    public void startTask() {
        try {
            // By creating a request, we get a handle to the putFile operation,
            // so we can cancel it later if we want to
            FileInputStream fis = new FileInputStream(file);
            String path = this.path + file.getName();
            request = api.putFileOverwriteRequest(path, fis, file.length(),
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
            getPrivate().setTaskError(new Error(e));
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
        request.abort();
    }

    //TODO: consider to create a servicetaskimpl (subclass of TaskImpl) to remove duplication

    @Override
    public HttpUrlConnectionBuilder getConnectionBulder() {
        return null;
    }

    @Override
    public String getResponse() {
        return null;
    }

    @Override
    public int getResponseCode() {
        return 0;
    }

    @Override
    public Error getCommandError() {
        return getTaskError();
    }

    @Override
    public boolean isCancelled() {
        return getTaskStatus() == Status.Cancelled;
    }

    @Override
    public void setServiceCommandCallback(final CommandCallback callback) {
        setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                callback.onCompleted(getCommandError());
            }
        });
    }

    @Override
    public ServiceCommand getServiceCommand() {
        return this;
    }
}
