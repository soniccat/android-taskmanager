package com.example.alexeyglushkov.wordteacher.main

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by alexeyglushkov on 19.06.16.
 */
object Preferences {

    // Quizlet Set / Term sort order

    // TODO: remove quizlet relation from here
    var quizletSetSortOrder: SortOrder
        get() = SortOrder.values()[readPreference.getInt("quizletSetSortOrder", SortOrder.BY_CREATE_DATE_INV.ordinal)]
        set(order) {
            val editor = writePreference
            editor.putInt("quizletSetSortOrder", order.ordinal)
            editor.commit()
        }

    var quizletTermSortOrder: SortOrder
        get() = SortOrder.values()[readPreference.getInt("quizletTermSortOrder", SortOrder.BY_NAME.ordinal)]
        set(order) {
            val editor = writePreference
            editor.putInt("quizletTermSortOrder", order.ordinal)
            editor.commit()
        }

    // course / card sort order

    var courseListSortOrder: SortOrder
        get() = SortOrder.values()[readPreference.getInt("courseSortOrder", SortOrder.BY_CREATE_DATE_INV.ordinal)]
        set(order) {
            val editor = writePreference
            editor.putInt("courseSortOrder", order.ordinal)
            editor.commit()
        }

    var cardListSortOrder: SortOrder
        get() = SortOrder.values()[readPreference.getInt("cardSortOrder", SortOrder.BY_NAME.ordinal)]
        set(order) {
            val editor = writePreference
            editor.putInt("cardSortOrder", order.ordinal)
            editor.commit()
        }

    //

    private val writePreference: SharedPreferences.Editor
        get() = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()

    private val readPreference: SharedPreferences
        get() = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    private val context: Context
        get() = MainApplication.instance.applicationContext

    private val name: String
        get() = "Pref"

    enum class SortOrder {
        BY_NAME,
        BY_NAME_INV,
        BY_CREATE_DATE,
        BY_CREATE_DATE_INV,
        BY_MODIFY_DATE,
        BY_MODIFY_DATE_INV,
        BY_PUBLISH_DATE,
        BY_PUBLISH_DATE_INV;

        val inverse: SortOrder
            get() {
                when (this) {
                    BY_NAME -> return BY_NAME_INV
                    BY_NAME_INV -> return BY_NAME
                    BY_CREATE_DATE -> return BY_CREATE_DATE_INV
                    BY_CREATE_DATE_INV -> return BY_CREATE_DATE
                    BY_MODIFY_DATE -> return BY_MODIFY_DATE_INV
                    BY_MODIFY_DATE_INV -> return BY_MODIFY_DATE
                    BY_PUBLISH_DATE -> return BY_PUBLISH_DATE_INV
                    BY_PUBLISH_DATE_INV -> return BY_PUBLISH_DATE
                    else -> return BY_NAME
                }
            }
    }
}
