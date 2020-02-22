package com.example.alexeyglushkov.wordteacher.quizletlistmodules.termlistmodule.presenter;

import android.os.Parcel;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.tools.LongTools;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;

public class QuizletTermCompareStrategy extends SortOrderCompareStrategy<QuizletTerm> {
    public static final Creator<QuizletTermCompareStrategy> CREATOR = new Creator<QuizletTermCompareStrategy>() {
        @Override
        public QuizletTermCompareStrategy createFromParcel(Parcel source) {
            return new QuizletTermCompareStrategy(source);
        }

        @Override
        public QuizletTermCompareStrategy[] newArray(int size) {
            return new QuizletTermCompareStrategy[size];
        }
    };

    public QuizletTermCompareStrategy(Preferences.SortOrder sortOrder) {
        super(sortOrder);
    }

    public QuizletTermCompareStrategy(Parcel in) {
        super(in);
    }

    @Override
    public int compare(QuizletTerm lhs, QuizletTerm rhs) {
        switch (getSortOrder()) {
            case BY_NAME_INV: return rhs.getTerm().compareToIgnoreCase(lhs.getTerm());
            case BY_CREATE_DATE: return LongTools.compare(lhs.getRank(), rhs.getRank());
            case BY_CREATE_DATE_INV: return LongTools.compare(rhs.getRank(), lhs.getRank());
            default:
                //BY_NAME
                return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
        }
    }
}
