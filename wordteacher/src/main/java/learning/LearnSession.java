package learning;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.UUID;

import model.Card;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class LearnSession implements Parcelable {
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<SessionCardResult> results = new ArrayList<>();
    private int currentIndex;

    public LearnSession(ArrayList<Card> cards) {
        this.cards.addAll(cards);

        for (Card card : this.cards) {
            SessionCardResult result = new SessionCardResult();
            result.cardId = card.getId();
            result.oldProgress = card.getFloatProgress();

            results.add(result);
        }
    }

    public LearnSession(Parcel parcel) {
        parcel.readTypedList(cards, Card.CREATOR);
        parcel.readTypedList(results, SessionCardResult.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cards);
        dest.writeTypedList(results);
        dest.writeInt(currentIndex);
    }

    public Card getCurrentCard() {
        return currentIndex < cards.size() ? cards.get(currentIndex) : null;
    }

    public Card getNextCard() {
        Card result = null;
        ++currentIndex;
        if (currentIndex < cards.size()) {
            result = cards.get(currentIndex);
        }

        return result;
    }

    public void updateProgress(Card card, boolean isRight) {
        SessionCardResult result = getCardResult(card);
        result.newProgress = card.getFloatProgress();
        result.isRight = isRight;
    }

    private SessionCardResult getCardResult(Card card) {
        for (SessionCardResult c : this.results) {
            if (card.getId().equals(c.cardId)) {
                return c;
            }
        }

        return null;
    }

    public ArrayList<Card> getRightAnsweredCards() {
        ArrayList<Card> resultArray = new ArrayList<>();
        for (int i=0; i<cards.size(); ++i) {
            SessionCardResult result = results.get(i);
            if (result.isRight) {
                resultArray.add(cards.get(i));
            }
        }

        return resultArray;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<LearnSession> CREATOR
            = new Parcelable.Creator<LearnSession>() {
        public LearnSession createFromParcel(Parcel in) {
            return new LearnSession(in);
        }

        public LearnSession[] newArray(int size) {
            return new LearnSession[size];
        }
    };

    public int getSize() {
        return cards.size();
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public SessionCardResult getCardResult(int i) {
        return results.get(i);
    }
}
