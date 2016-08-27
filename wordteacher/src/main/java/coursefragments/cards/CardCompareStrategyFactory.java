package coursefragments.cards;

import main.Preferences;
import model.Card;
import tools.SortOrderCompareStrategyFactory;

/**
 * Created by alexeyglushkov on 27.08.16.
 */
public class CardCompareStrategyFactory extends SortOrderCompareStrategyFactory<Card> {
    @Override
    protected int compare(Card lhs, Card rhs, Preferences.SortOrder sortOrder) {
        switch (sortOrder) {
            case BY_NAME_INV: return rhs.getTerm().compareToIgnoreCase(lhs.getTerm());
            case BY_CREATE_DATE: return lhs.getCreateDate().compareTo(rhs.getCreateDate());
            case BY_CREATE_DATE_INV: return rhs.getCreateDate().compareTo(lhs.getCreateDate());
            default:
                //BY_NAME
                return lhs.getTerm().compareToIgnoreCase(rhs.getTerm());
        }
    }
}
