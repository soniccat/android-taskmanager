package com.example.alexeyglushkov.tools;

import android.os.Bundle;
import android.util.SparseArray;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public final class SparceArrayTools {
    public static void storeSparceArray(SparseArray<String> array, Bundle bundle, int id) {
        Bundle arrayBundle = new Bundle(bundle.getClassLoader());

        int size = array != null ? array.size() : 0;
        arrayBundle.putInt("sparceArrayLength", size);
        for (int i = 0; i < array.size(); i++) {
            arrayBundle.putInt("sparceArrayKey" + i, array.keyAt(i));
            arrayBundle.putString("sparceArrayValue" + i, array.valueAt(i));
        }

        bundle.putBundle("sparceArray" + id, arrayBundle);
    }

    public static SparseArray<String> readSparceArray(Bundle bundle, int id) {
        Bundle arrayBundle = bundle.getBundle("sparceArray" + id);

        SparseArray<String> result = new SparseArray<>();
        int len = arrayBundle.getInt("sparceArrayLength");
        for (int i=0; i<len; ++i) {
            int key = arrayBundle.getInt("sparceArrayKey" + i);
            String value = arrayBundle.getString("sparceArrayValue" + i);
            result.put(key, value);
        }

        return result;
    }
}
