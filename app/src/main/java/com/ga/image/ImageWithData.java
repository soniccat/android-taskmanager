package com.ga.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ga.loader.data.DataHandler;
import com.ga.task.DataFormat;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 11.10.14.
 */
public class ImageWithData extends Image {
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public DataHandler getDataHandler() {
        return new DataHandler() {
            @Override
            public Error handleData(ByteArrayBuffer data) {
                return loadData(data);
            }
        };
    }

    private Error loadData(ByteArrayBuffer data) {
        bitmap = Image.bitmapFromByteArray(data);
        return null;
    }
}
