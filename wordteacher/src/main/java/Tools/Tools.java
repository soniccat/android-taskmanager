package tools;

import android.os.Bundle;
import android.util.SparseArray;

import java.util.concurrent.TimeUnit;

/**
 * Created by alexeyglushkov on 12.06.16.
 */
public class Tools {
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

    public static String getDurationString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(" days");
        } else if (hours > 0) {
            sb.append(hours);
            sb.append(" hours");
        } else if (minutes > 0) {
            sb.append(minutes);
            sb.append(" minutes");
        } else if (seconds > 0) {
            sb.append(seconds);
            sb.append(" seconds");
        }

        return sb.toString();
    }
}
