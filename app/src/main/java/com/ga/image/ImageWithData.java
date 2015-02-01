package com.ga.image;

import android.graphics.Bitmap;
import com.ga.loader.data.ByteArrayHandler;

import org.apache.http.util.ByteArrayBuffer;

/**
 * Created by alexeyglushkov on 11.10.14.
 */
public class ImageWithData extends Image {
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
