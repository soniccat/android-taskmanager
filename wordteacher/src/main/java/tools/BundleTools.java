package tools;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 25.09.16.
 */
public class BundleTools {
    public static void storeBundles(Bundle bundle, String tag, List<Bundle> bundles) {
        bundle.putInt(tag + "Count", bundles.size());

        for (int i=0; i < bundles.size(); ++i) {
            Bundle b = bundles.get(i);
            bundle.putBundle(tag + Integer.toString(i), b);
        }
    }

    public static List<Bundle> restoreBundles(Bundle bundle, String tag) {
        List<Bundle> result = new ArrayList<>();

        int count = bundle.getInt(tag + "Count");
        for (int i = 0; i < count; ++i) {
            Bundle b = bundle.getBundle(tag + Integer.toString(i));
            result.add(b);
        }

        return result;
    }
}
