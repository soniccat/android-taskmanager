package quizletfragments;

import main.Preferences;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public interface QuizletSortable {
    void setSortOrder(Preferences.SortOrder sortOrder);
    Preferences.SortOrder getSortOrder();
}
