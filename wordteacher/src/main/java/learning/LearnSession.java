package learning;

import java.util.ArrayList;
import java.util.UUID;

import model.Card;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class LearnSession {
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<CardResult> results = new ArrayList<>();
    private int currentIndex;

    public LearnSession(ArrayList<Card> cards) {
        this.cards.addAll(cards);

        for (Card card : this.cards) {
            CardResult result = new CardResult();
            result.cardId = card.getId();
            result.oldProgress = card.getFloatProgress();
        }
    }

    public Card getCurrentCard() {
        return cards.get(currentIndex);
    }

    public Card getNextCard() {
        Card result = null;
        ++currentIndex;
        if (currentIndex < cards.size()) {
            result = cards.get(currentIndex);
        }

        return result;
    }

    public void updateProgress(Card card) {
        getCardResult(card).newProgress = card.getFloatProgress();
    }

    private CardResult getCardResult(Card card) {
        for (CardResult c : this.results) {
            if (card.getId().equals(c.cardId)) {
                return c;
            }
        }

        return null;
    }

    private class CardResult {
        public UUID cardId;
        public float oldProgress;
        public float newProgress;
    }
}
