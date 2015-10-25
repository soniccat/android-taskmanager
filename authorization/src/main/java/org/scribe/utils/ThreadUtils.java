package org.scribe.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by alexeyglushkov on 25.10.15.
 */
public class ThreadUtils {
    public static void runOnMainThread(Runnable runnable) {
        Looper mainLooper = Looper.getMainLooper();
        Handler hd = new Handler(mainLooper);
        hd.post(runnable);
    }
}
