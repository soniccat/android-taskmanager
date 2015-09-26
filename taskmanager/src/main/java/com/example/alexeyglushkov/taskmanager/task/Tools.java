package com.example.alexeyglushkov.taskmanager.task;

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

    public static void runOnMainThread(Runnable runnable) {
        Looper mainLooper = Looper.getMainLooper();
        Handler hd = new Handler(mainLooper);
        hd.post(runnable);
    }

    public static int reverseIntCompare(int lhs, int rhs) {
        if (lhs > rhs) {
            return -1;
        }

        if (lhs == rhs) {
            return 0;
        }

        return 1;
    }
}
