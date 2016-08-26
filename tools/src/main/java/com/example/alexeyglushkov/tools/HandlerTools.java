package com.example.alexeyglushkov.tools;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public final class HandlerTools {
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

    public static void runOnMainThread(Runnable runnable) {
        runOnMainThreadDelayed(runnable, 0);
    }

    public static void runOnMainThreadDelayed(Runnable runnable, long delay) {
        Looper mainLooper = Looper.getMainLooper();
        Handler hd = new Handler(mainLooper);
        hd.postDelayed(runnable, delay);
    }
}
