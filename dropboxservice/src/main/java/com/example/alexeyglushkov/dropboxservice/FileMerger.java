package com.example.alexeyglushkov.dropboxservice;

import java.io.File;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public interface FileMerger {
    File merge(File f1, File f2) throws Exception;
}
