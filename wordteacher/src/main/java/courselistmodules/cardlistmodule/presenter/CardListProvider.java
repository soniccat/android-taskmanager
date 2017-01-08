package courselistmodules.cardlistmodule.presenter;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import listmodule.SimpleStorableListProvider;
import model.Card;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 24.09.16.
 */
public class CardListProvider extends SimpleStorableListProvider<Card> {
    public static final String STORE_CARD_IDS = "STORE_COURSE_IDS";

    //// Initialize
    
    public CardListProvider(List<Card> items) {
        super(items);
    }

    public CardListProvider(Bundle bundle, Object context) {
        super(null);
        restore(bundle, context);
    }

    @Override
    public void store(Bundle bundle) {
        bundle.putStringArrayList(STORE_CARD_IDS, getCardIds(items));
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        List<String> idArray = bundle.getStringArrayList(STORE_CARD_IDS);
        items = getCards(idArray, (CourseHolder) context);
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(STORE_CARD_IDS);
    }

    //// Getters

    private ArrayList<String> getCardIds(List<Card> cards) {
        final ArrayList<String> cardIds = new ArrayList<>();
        for (Card card : cards) {
            cardIds.add(card.getId().toString());
        }

        return cardIds;
    }

    private List<Card> getCards(List<String> cardIds, CourseHolder courseHolder) {
        final List<Card> cards = new ArrayList<>();
        for (String udid : cardIds) {
            Card card = courseHolder.getCard(UUID.fromString(udid));
            if (card != null) {
                cards.add(card);
            }
        }

        return cards;
    }
}
