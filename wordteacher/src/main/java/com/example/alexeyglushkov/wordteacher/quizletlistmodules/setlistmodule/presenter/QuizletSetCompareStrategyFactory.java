package com.example.alexeyglushkov.wordteacher.quizletlistmodules.setlistmodule.presenter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import com.example.alexeyglushkov.wordteacher.main.Preferences;
import com.example.alexeyglushkov.wordteacher.tools.LongTools;
import com.example.alexeyglushkov.wordteacher.tools.SortOrderCompareStrategyFactory;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class QuizletSetCompareStrategyFactory extends SortOrderCompareStrategyFactory<QuizletSet> {

    protected int compare(QuizletSet lhs, QuizletSet rhs, Preferences.SortOrder sortOrder) {
        switch (sortOrder) {
            case BY_NAME_INV: return rhs.getTitle().compareToIgnoreCase(lhs.getTitle());
            case BY_CREATE_DATE: return LongTools.compare(lhs.getCreateDate(), rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return LongTools.compare(rhs.getCreateDate(), lhs.getCreateDate());
            default:
                //BY_NAME
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
        }
    }
}
