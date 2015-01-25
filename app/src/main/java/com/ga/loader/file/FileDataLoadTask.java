package com.ga.loader.file;

import android.content.Context;

import com.ga.loader.data.ByteArrayBufferHandler;
import com.ga.loader.data.InputStreamHandler;
import com.ga.loader.data.InputStreamToByteArrayBufferHandler;

import org.apache.http.util.ByteArrayBuffer;

import java.io.InputStream;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class FileDataLoadTask extends FileLoadTask {
    public FileDataLoadTask(String fileName, final ByteArrayBufferHandler dataHandler, Context context) {
        super(fileName, null, context);
        setHandler(new InputStreamToByteArrayBufferHandler(new ByteArrayBufferHandler() {
            @Override
            public Error handleByteArrayBuffer(ByteArrayBuffer byteArray) {
                return dataHandler.handleByteArrayBuffer(byteArray);
            }
        }));
    }

    protected InputStreamToByteArrayBufferHandler geHandler() {
        return (InputStreamToByteArrayBufferHandler)handler;
    }

    @Override
    protected Error handleStream(InputStream fis) {
        geHandler().setProgressUpdater(createProgressUpdater(getFileSize()));
        return super.handleStream(fis);
    }
}
