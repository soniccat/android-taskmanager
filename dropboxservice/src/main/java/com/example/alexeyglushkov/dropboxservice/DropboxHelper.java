package com.example.alexeyglushkov.dropboxservice;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxServerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by alexeyglushkov on 04.09.16.
 */
public class DropboxHelper {
    private DropboxAPI<?> api;

    public DropboxHelper(DropboxAPI<?> api) {
        super();
        this.api = api;
    }

    // TODO: think about listener, should it be per file or for the directory
    // think about request access to cancel
    public void downloadFileOrDir(String srcPath, String destPath, ProgressListener listener) throws FileNotFoundException, DropboxException {
        DropboxAPI.Entry entry = api.metadata(srcPath, 0, null, true, null);
        if (entry.isDir) {
            File f = new File(destPath);
            if (!f.exists()) {
                f.mkdir();
            }

            for (DropboxAPI.Entry e : entry.contents) {
                downloadFileOrDir(e.path, addPathName(f.getPath(), e.fileName()), listener);
            }
        } else {
            downloadFile(srcPath, destPath, listener);
        }
    }

    public void downloadFile(String srcPath, String destPath, ProgressListener listener) throws FileNotFoundException, DropboxException {
        File file = new File(destPath);
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(file);

        try {
            api.getFile(srcPath, null, outputStream, listener);
        } catch (DropboxException e) {
            e.printStackTrace();

        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: think about listener, should it be per file or for the directory
    // think about request access to cancel
    public void uploadFileOrDirectory(String srcPath, String dstPath, ProgressListener listener) throws FileNotFoundException, DropboxException {
        File srcFile = new File(srcPath);
        if (srcFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            for (File f : files) {
                uploadFileOrDirectory(f.getPath(), addPathName(dstPath, f.getName()), listener);
            }
        } else {
            uploadFile(srcPath, dstPath, listener);
        }
    }

    public DropboxAPI.UploadRequest uploadFile(String srcPath, String dstPath, ProgressListener listener) throws FileNotFoundException, DropboxException {
        DropboxAPI.UploadRequest request = null;

        // By creating a request, we get a handle to the putFile operation,
        // so we can cancel it later if we want to
        File file = new File(srcPath);
        FileInputStream fis = new FileInputStream(file);

        try {
            request = api.putFileOverwriteRequest(dstPath, fis, file.length(), listener);
            if (request != null) {
                request.upload();
            }

        } catch (DropboxException e) {
            e.printStackTrace();
            throw e;

        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return request;
    }

    public void deleteFile(String path, ProgressListener listener) throws DropboxException{
        api.delete(path);
    }

    public String addPathName(String path, String name) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }

        path = path + name;
        return path;
    }
}
