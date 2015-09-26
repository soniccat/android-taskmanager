package com.example.alexeyglushkov.testsupport;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

/**
 * Created by alexeyglushkov on 26.09.15.
 */
public class InstrumentationRunner extends InstrumentationTestRunner {

    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);
        System.setProperty("dexmaker.dexcache", getTargetContext().getCacheDir().getPath());
    }
}
