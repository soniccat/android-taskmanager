package quizletlistmodules.termlistmodule.presenter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import main.Preferences;
import tools.LongTools;
import tools.SortOrderCompareStrategyFactory;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public class QuizletTermCompareStrategyFactory extends SortOrderCompareStrategyFactory<QuizletTerm> {
    @Override
    protected int compare(QuizletTerm lhs, QuizletTerm rhs, Preferences.SortOrder sortOrder) {
        switch (sortOrder) {
            case BY_NAME_INV: return rhs.getTerm().compareToIgnoreCase(lhs.getTerm());
            case BY_CREATE_DATE: return LongTools.compare(lhs.getRank(), rhs.getRank());
            case BY_CREATE_DATE_INV: return LongTools.compare(rhs.getRank(), lhs.getRank());
            default:
                //BY_NAME
                return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
        }
    }
}
