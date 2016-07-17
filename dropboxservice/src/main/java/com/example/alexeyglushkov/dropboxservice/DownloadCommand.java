package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.example.alexeyglushkov.authorization.Auth.ServiceCommand;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by alexeyglushkov on 17.07.16.
 */
public class DownloadCommand extends ServiceTask {
    private DropboxAPI<?> api;
    private String srcPath;
    private String destPath;

    public DownloadCommand(DropboxAPI<?> api, String srcPath, String destPath) {
        this.api = api;
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    @Override
    public void startTask() {
        downloadFileOrDir(srcPath, destPath);
        getPrivate().handleTaskCompletion();
    }

    private void downloadFileOrDir(String srcPath, String destPath) {
        FileOutputStream outputStream = null;
        try {
            DropboxAPI.Entry entry = api.metadata(srcPath, 0, null, true, null);
            if (entry.isDir) {
                File f = new File(addPathName(destPath, entry.fileName()));
                f.mkdir();

                for (DropboxAPI.Entry e : entry.contents) {
                    downloadFileOrDir(e.path, addPathName(f.getPath(), e.fileName()));
                }
            } else {
                File file = new File(destPath);
                outputStream = new FileOutputStream(file);

                api.getFile(srcPath, null, outputStream, new com.dropbox.client2.ProgressListener() {
                    @Override
                    public void onProgress(long bytes, long total) {
                        DownloadCommand.this.triggerProgressListeners(bytes, total);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            getPrivate().setTaskError(new Error(e));
        }
    }

    private String addPathName(String path, String name) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        path = path + name;
        return path;
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
}
