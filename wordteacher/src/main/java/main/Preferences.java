package main;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alexeyglushkov on 19.06.16.
 */
public class Preferences {
    public enum SortOrder{
        BY_NAME,
        BY_CREATE_DATE,
        BY_MODIFY_DATE,
        BY_PUBLISH_DATE
    }

    public static SortOrder getSortOrder() {
        return SortOrder.values()[getReadPreference().getInt("sortOrder", 1)];
    }

    public static void setSortOrder(SortOrder order) {
        getWritePreference().putInt("sortOrder", order.ordinal());
    }

    private static SharedPreferences.Editor getWritePreference() {
        return getContext().getSharedPreferences(getName(), Context.MODE_PRIVATE).edit();
    }

    private static SharedPreferences getReadPreference() {
        return getContext().getSharedPreferences(getName(), Context.MODE_PRIVATE);
    }

    private static Context getContext() {
        return MainApplication.instance.getApplicationContext();
    }

    private static String getName() {
        return "Pref";
    }
}
