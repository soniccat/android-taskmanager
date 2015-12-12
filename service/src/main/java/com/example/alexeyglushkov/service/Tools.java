package com.example.alexeyglushkov.service;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alexeyglushkov on 12.12.15.
 */
public class Tools {
    public static void runOnHandlerThread(Handler handler, final Runnable action) {
        if (Looper.myLooper() == handler.getLooper()) {
            action.run();
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    action.run();
                }
            });
        }
    }
}
