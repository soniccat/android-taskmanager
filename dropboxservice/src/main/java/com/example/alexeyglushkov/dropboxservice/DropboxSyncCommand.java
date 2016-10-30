package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.example.alexeyglushkov.authtaskmanager.IServiceTask;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

import junit.framework.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by alexeyglushkov on 04.09.16.
 */
public class DropboxSyncCommand extends ServiceTask implements IServiceTask {
    private @NonNull String localPath;
    private @NonNull String dropboPath;
    private long lastSyncDate;

    private @NonNull DropboxAPI<?> api;
    private @NonNull DropboxHelper helper;

    private @Nullable DropboxCommandProvider.SyncCallback callback;

    //// Initialize

    public DropboxSyncCommand(@NonNull DropboxAPI<?> api, @NonNull String localPath, @NonNull String dropboPath, long lastSyncDate) {
        Assert.assertNotNull(api);
        Assert.assertNotNull(localPath);
        Assert.assertNotNull(dropboPath);

        this.api = api;
        this.helper = new DropboxHelper(api);
        this.localPath = localPath;
        this.dropboPath = dropboPath;
        this.lastSyncDate = lastSyncDate;
    }

    //// Actions

    @Override
    public void startTask(Callback callback) {
        try {
            syncFileOrDir(localPath, dropboPath, getProgressListener());

        } catch (Exception e) {
            e.printStackTrace();
            setTaskError(new Error("Dropbox Sync Error", e));
        }

        getPrivate().handleTaskCompletion(callback);
    }

    // src - local path, dest - dropbox path
    private void syncFileOrDir(@NonNull String localPath, @NonNull String dropboxPath, @Nullable com.dropbox.client2.ProgressListener listener) throws Exception {
        File srcFile = new File(localPath);
        DropboxAPI.Entry entry = loadMetadata(dropboxPath);

        // treat 404 as a signal to upload
        if (entry == null) {
            helper.uploadFileOrDirectory(localPath, dropboxPath, listener);

        } else if (srcFile.isDirectory() && entry.isDir) {
            syncDir(srcFile, entry, listener);

        } else if (!srcFile.isDirectory() && !entry.isDir) {
            syncFile(srcFile, entry, listener);

        } else {
            // TODO:
        }
    }

    @Nullable
    private DropboxAPI.Entry loadMetadata(@NonNull String dropboxPath) throws DropboxException {
        DropboxAPI.Entry entry = null;
        try {
            entry = api.metadata(dropboxPath, 0, null, true, null);
        } catch (DropboxServerException e) {
            if (e.error != DropboxServerException._404_NOT_FOUND) {
                throw e;
            }
        }

        if (entry != null && entry.isDeleted) {
            entry = null;
        }

        return entry;
    }

    private void syncDir(@NonNull File localDir, @NonNull DropboxAPI.Entry dropboxEntry, @Nullable com.dropbox.client2.ProgressListener listener) throws Exception {
        updateLocalDir(localDir, dropboxEntry, listener);
        updateDropboxDir(localDir, dropboxEntry, listener);
    }

    private void updateDropboxDir(@NonNull File localDir, @NonNull DropboxAPI.Entry dropboxEntry, @Nullable com.dropbox.client2.ProgressListener listener) throws Exception {
        // download part
        for (DropboxAPI.Entry childEntry : dropboxEntry.contents) {
            File file = getChild(localDir, childEntry.fileName());

            if (file != null) {
                if (file.isDirectory() && childEntry.isDir) {
                    syncDir(file, childEntry, listener);

                } else if (!file.isDirectory() && !childEntry.isDir) {
                    syncFile(file, childEntry, listener);

                } else {
                    // TODO
                }

            } else {
                long dropboxDate = getModifiedTime(childEntry);

                if (dropboxDate > lastSyncDate) {
                    helper.downloadFileOrDir(childEntry.path, helper.addPathName(localDir.getPath(), childEntry.fileName()), listener);

                } else {
                    helper.deleteFile(childEntry.path, listener);
                }
            }
        }
    }

    private void updateLocalDir(@NonNull File localDir, @NonNull DropboxAPI.Entry dropboxEntry, @Nullable com.dropbox.client2.ProgressListener listener) throws Exception {
        // upload part
        for (File file : localDir.listFiles()) {
            DropboxAPI.Entry childEntry = getChild(dropboxEntry, file.getName());

            if (childEntry != null) {
                childEntry = validateEntry(dropboxEntry, childEntry);

                if (file.isDirectory() && childEntry.isDir) {
                    syncDir(file, childEntry, listener);

                } else if (!file.isDirectory() && !childEntry.isDir) {
                    syncFile(file, childEntry, listener);

                } else {
                    // TODO
                }

            } else {
                long localDate = file.lastModified();

                if (localDate > lastSyncDate) {
                    helper.uploadFileOrDirectory(file.getPath(), helper.addPathName(dropboxEntry.path, file.getName()), listener);

                } else {
                    deleteDirectory(file);
                }
            }
        }
    }

    @Nullable
    private DropboxAPI.Entry validateEntry(@NonNull DropboxAPI.Entry dropboxEntry, @NonNull DropboxAPI.Entry childEntry) throws DropboxException {
        DropboxAPI.Entry result = dropboxEntry;

        if (childEntry.isDir && childEntry.contents == null) {
            result = loadMetadata(dropboxEntry.path);
        }

        if (result != null && result.isDeleted) {
            result.contents = new ArrayList<>();
        }

        return result;
    }

    private long getModifiedTime(@NonNull DropboxAPI.Entry dropboxEntry) {
        return RESTUtility.parseDate(dropboxEntry.modified).getTime();
    }

    private void syncFile(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxFile, @Nullable com.dropbox.client2.ProgressListener listener) throws Exception {
        long localDate = localFile.lastModified();
        long dropboxDate = getModifiedTime(dropboxFile);

        if (localDate > lastSyncDate && dropboxDate > lastSyncDate) {
            if (callback != null) {
                merge(localFile, dropboxFile, listener);
            }

        } else if (localDate > lastSyncDate) {
            helper.uploadFile(localFile.getPath(), dropboxFile.path, listener);

        } else if (dropboxDate > lastSyncDate) {
            helper.downloadFile(dropboxFile.path, localFile.getPath(), listener);
        }
    }

    private void merge(@NonNull File localFile, @NonNull DropboxAPI.Entry dropboxFile, com.dropbox.client2.ProgressListener listener) throws Exception {
        Assert.assertNotNull(callback);

        final ArrayList<Object> container = new ArrayList<>();

        final Semaphore semaphore = new Semaphore(0, true);
        callback.merge(localFile, dropboxFile, new DropboxCommandProvider.MergeCompletion() {
            @Override
            public void completed(File result, Error error) {
                if (error != null) {
                    container.add(error);
                } else {
                    container.add(result);
                }

                semaphore.release();
            }
        });

        semaphore.acquire();

        Object result = container.size() > 0 ? container.get(0) : null;
        if (result != null && result instanceof File) {
            File file = (File)result;
            propagateFile(file, localFile.getPath(), dropboxFile.path, listener);

            // TODO: move to helper like safeDelete
            try {
                file.delete();
            } catch (Exception ex) {
            }

        } else if (result != null && result instanceof Error) {
            throw (Error)result;
        }
    }

    private void propagateFile(File file, String localPath, String dropboxPath, com.dropbox.client2.ProgressListener listener) throws Exception {
        helper.uploadFile(file.getPath(), dropboxPath, listener);

        File localFile = new File(localPath);
        localFile.delete();

        file.renameTo(localFile);
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

            @Override
            public boolean isFinished() {
                return getNormalizedValue() == 1.0f;
            }
        };

        getPrivate().triggerProgressListeners(info);
    }

    // TODO: move to tools
    private static boolean deleteDirectory(@NonNull File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if(null != files) {
                for (File file : files) {
                    if(file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return(directory.delete());
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
                DropboxSyncCommand.this.triggerProgressListeners(bytes, total);
            }
        };
    }

    //// Setter

    public void setCallback(@Nullable DropboxCommandProvider.SyncCallback callback) {
        this.callback = callback;
    }

    //// Getter

    private DropboxAPI.Entry getChild(@NonNull DropboxAPI.Entry entry, @NonNull String name) {
        DropboxAPI.Entry result = null;

        for (DropboxAPI.Entry childEntry : entry.contents) {
            if (childEntry.fileName().equals(name)) {
                result = childEntry;
                break;
            }
        }

        return result;
    }

    private File getChild(@NonNull File dir, @NonNull String name) {
        File result = null;

        for (File file : dir.listFiles()) {
            if (file.getName().equals(name)) {
                result = file;
                break;
            }
        }

        return result;
    }
}
