package com.ga.image;

import android.graphics.Bitmap;
import com.ga.loader.data.ByteArrayBufferHandler;
import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 11.10.14.
 */
public class ImageWithData extends Image {
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ByteArrayBufferHandler getDataHandler() {
        return new ByteArrayBufferHandler() {
            @Override
            public Error handleByteArrayBuffer(ByteArrayBuffer byteArray) {
                return loadData(byteArray);
            }
        };
    }

    private Error loadData(ByteArrayBuffer data) {
        bitmap = Image.bitmapFromByteArray(data);
        return null;
    }
}
