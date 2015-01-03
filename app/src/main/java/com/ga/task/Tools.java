package com.ga.task;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alexeyglushkov on 31.12.14.
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

    public static void postOnMainLoop(Runnable runnable) {
        Looper mainLooper = Looper.getMainLooper();
        Handler hd = new Handler(mainLooper);
        hd.post(runnable);
    }

    public static int compare(int lhs, int rhs) {
        return lhs < rhs ? 1 : (lhs == rhs ? 0 : -1);
    }
}
