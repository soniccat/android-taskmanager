package main;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by alexeyglushkov on 19.06.16.
 */
public class Preferences {
    public enum SortOrder{
        BY_NAME,
        BY_NAME_INV,
        BY_CREATE_DATE,
        BY_CREATE_DATE_INV,
        BY_MODIFY_DATE,
        BY_MODIFY_DATE_INV,
        BY_PUBLISH_DATE,
        BY_PUBLISH_DATE_INV;

        public SortOrder getInverse() {
            switch (this) {
                case BY_NAME: return BY_NAME_INV;
                case BY_NAME_INV: return BY_NAME;
                case BY_CREATE_DATE: return BY_CREATE_DATE_INV;
                case BY_CREATE_DATE_INV: return BY_CREATE_DATE;
                case BY_MODIFY_DATE: return BY_MODIFY_DATE_INV;
                case BY_MODIFY_DATE_INV: return BY_MODIFY_DATE;
                case BY_PUBLISH_DATE: return BY_PUBLISH_DATE_INV;
                case BY_PUBLISH_DATE_INV: return BY_PUBLISH_DATE;
                default:
                    return BY_NAME;
            }
        }
    }

    public static SortOrder getQuizletSetSortOrder() {
        return SortOrder.values()[getReadPreference().getInt("quizletSetSortOrder", SortOrder.BY_CREATE_DATE_INV.ordinal())];
    }

    public static void setQuizletSetSortOrder(SortOrder order) {
        SharedPreferences.Editor editor = getWritePreference();
        editor.putInt("quizletSetSortOrder", order.ordinal());
        editor.commit();
    }

    public static SortOrder getQuizletTermSortOrder() {
        return SortOrder.values()[getReadPreference().getInt("quizletTermSortOrder", SortOrder.BY_NAME.ordinal())];
    }

    public static void setQuizletTermSortOrder(SortOrder order) {
        SharedPreferences.Editor editor = getWritePreference();
        editor.putInt("quizletTermSortOrder", order.ordinal());
        editor.commit();
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
