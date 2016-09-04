package com.example.alexeyglushkov.dropboxservice;

import android.support.annotation.Nullable;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.RESTUtility;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;
import com.example.alexeyglushkov.authtaskmanager.IServiceTask;
import com.example.alexeyglushkov.authtaskmanager.ServiceTask;
import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;

import java.io.File;

/**
 * Created by alexeyglushkov on 04.09.16.
 */
public class DropboxSyncCommand extends ServiceTask implements IServiceTask {
    private String localPath;
    private String dropboPath;
    private long lastSyncDate;

    private DropboxAPI<?> api;
    private DropboxHelper helper;

    public DropboxSyncCommand(DropboxAPI<?> api, String localPath, String dropboPath, long lastSyncDate) {
        this.api = api;
        this.helper = new DropboxHelper(api);
        this.localPath = localPath;
        this.dropboPath = dropboPath;
        this.lastSyncDate = lastSyncDate;
    }

    @Override
    public void startTask() {
        boolean needDelay = false;

        try {
            syncFileOrDir(localPath, dropboPath, getProgressListener());
            needDelay = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (needDelay) {
            try {
                // sleep to handle different later file modification date
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        getPrivate().handleTaskCompletion();
    }

    // src - local path, dest - dropbox path
    private void syncFileOrDir(String localPath, String dropboxPath, com.dropbox.client2.ProgressListener listener) throws Exception {
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

        //if (entry.isDir) {
    }

    @Nullable
    private DropboxAPI.Entry loadMetadata(String dropboxPath) throws DropboxException {
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

    private void syncDir(File localDir, DropboxAPI.Entry dropboxEntry, com.dropbox.client2.ProgressListener listener) throws Exception {
        long localDirDate = localDir.lastModified();
        long dropboxDirDate = getModifiedTime(dropboxEntry);

        // upload part
        for (File file : localDir.listFiles()) {
            DropboxAPI.Entry childEntry = getChild(dropboxEntry, file.getName());

            if (childEntry != null) {
                if (file.isDirectory() && childEntry.isDir) {
                    syncDir(file, childEntry, listener);

                } else if (!file.isDirectory() && !childEntry.isDir) {
                    syncFile(file, childEntry, listener);

                } else {
                    // TODO
                }

            } else {
                long localDate = file.lastModified();

                if (localDate > dropboxDirDate) {
                    if (localDate > lastSyncDate && dropboxDirDate <= lastSyncDate) {
                        helper.uploadFileOrDirectory(file.getPath(), helper.addPathName(dropboxEntry.path, file.getName()), listener);
                    } else {
                        // TODO
                    }
                } else if (dropboxDirDate > localDate) {
                    if (dropboxDirDate > lastSyncDate && localDate <= lastSyncDate) {
                        deleteDirectory(file);

                    } else {
                        // TODO
                    }
                }
            }
        }

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

                if (dropboxDate > localDirDate) {
                    if (dropboxDate > lastSyncDate && localDirDate <= lastSyncDate) {
                        helper.downloadFileOrDir(childEntry.path, helper.addPathName(localDir.getPath(), childEntry.fileName()), listener);
                    } else {
                        // TODO
                    }
                } else if (localDirDate > dropboxDate) {
                    if (localDirDate > lastSyncDate && dropboxDate <= lastSyncDate) {
                        helper.deleteFile(childEntry.path, listener);

                    } else {
                        // TODO:
                    }
                }
            }
        }
    }

    private long getModifiedTime(DropboxAPI.Entry dropboxEntry) {
        return RESTUtility.parseDate(dropboxEntry.modified).getTime();
    }

    private void syncFile(File localFile, DropboxAPI.Entry dropboxFile, com.dropbox.client2.ProgressListener listener) throws Exception {
        long localDate = localFile.lastModified();
        long dropboxDate = getModifiedTime(dropboxFile);

        if (localDate > dropboxDate) {
            if (localDate > lastSyncDate && dropboxDate <= lastSyncDate) {
                helper.uploadFile(localFile.getPath(), dropboxFile.path, listener);
            } else {
                // TODO
            }
        } else if (dropboxDate > localDate) {
            if (dropboxDate > lastSyncDate && localDate <= lastSyncDate) {
                helper.downloadFile(dropboxFile.path, localFile.getPath(), listener);
            } else {
                // TODO
            }
        }
    }

    private DropboxAPI.Entry getChild(DropboxAPI.Entry entry, String name) {
        DropboxAPI.Entry result = null;

        for (DropboxAPI.Entry childEntry : entry.contents) {
            if (childEntry.fileName().equals(name)) {
                result = childEntry;
                break;
            }
        }

        return result;
    }

    private File getChild(File dir, String name) {
        File result = null;

        for (File file : dir.listFiles()) {
            if (file.getName().equals(name)) {
                result = file;
                break;
            }
        }

        return result;
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

    // TODO: move to tools
    private static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if(null!=files) {
                for (int i=0; i<files.length; i++) {
                    if(files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    }
                    else {
                        files[i].delete();
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
}
