package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import android.os.Parcel;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.tools.LongTools;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategy;

public class QuizletSetCompareStrategy extends SortOrderCompareStrategy<QuizletSet> {
    public static final Creator<QuizletSetCompareStrategy> CREATOR = new Creator<QuizletSetCompareStrategy>() {
        @Override
        public QuizletSetCompareStrategy createFromParcel(Parcel source) {
            return new QuizletSetCompareStrategy(source);
        }

        @Override
        public QuizletSetCompareStrategy[] newArray(int size) {
            return new QuizletSetCompareStrategy[size];
        }
    };

    public QuizletSetCompareStrategy(Preferences.SortOrder sortOrder) {
        super(sortOrder);
    }

    public QuizletSetCompareStrategy(Parcel in) {
        super(in);
    }

    @Override
    public int compare(QuizletSet lhs, QuizletSet rhs) {
        switch (getSortOrder()) {
            case BY_NAME_INV:
                return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE:
                return LongTools.compare(lhs.getCreateDate(), rhs.getCreateDate());
            case BY_CREATE_DATE_INV:
                return LongTools.compare(rhs.getCreateDate(), lhs.getCreateDate());
            default:
                //BY_NAME
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
    }
}
