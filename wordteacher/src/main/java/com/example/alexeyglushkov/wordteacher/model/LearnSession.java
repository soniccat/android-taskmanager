package com.example.alexeyglushkov.wordteacher.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter.CardListProvider;
import com.example.alexeyglushkov.wordteacher.learningmodule.SessionCardResult;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class LearnSession implements Parcelable {
    private List<Card> cards = new ArrayList<>();
    private ArrayList<SessionCardResult> results = new ArrayList<>();
    private int currentIndex;

    //// Initialization

    public LearnSession(ArrayList<Card> cards) {
        this.cards.addAll(cards);

        for (Card card : this.cards) {
            SessionCardResult result = new SessionCardResult();
            result.cardId = card.getId();
            result.oldProgress = card.getFloatProgress();

            results.add(result);
        }
    }

    public LearnSession(Bundle bundle, CourseHolder holder) {
        restore(bundle, holder);
    }

    public LearnSession(Parcel parcel) {
        parcel.readTypedList(cards, Card.CREATOR);
        parcel.readTypedList(results, SessionCardResult.CREATOR);
        currentIndex = parcel.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(cards);
        dest.writeTypedList(results);
        dest.writeInt(currentIndex);
    }

    public void store(Bundle bundle) {
        CardListProvider cardListProvider = new CardListProvider(cards);
        cardListProvider.store(bundle);
        bundle.putParcelableArrayList("results", results);
        bundle.putInt("currentIndex", currentIndex);
    }

    public void restore(Bundle bundle, CourseHolder courseHolder) {
        CardListProvider cardListProvider = new CardListProvider(bundle, courseHolder);
        cards = cardListProvider.getList();
        results = bundle.getParcelableArrayList("results");
        currentIndex = bundle.getInt("currentIndex");
    }

    //// Actions

    public void updateProgress(Card card, boolean isRight) {
        SessionCardResult result = getCardResult(card);
        result.newProgress = card.getFloatProgress();
        result.isRight = isRight;
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

    //// Getters

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

    public int getSize() {
        return cards.size();
    }

    public Card getCard(int i) {
        return cards.get(i);
    }

    public SessionCardResult getCardResult(int i) {
        return results.get(i);
    }

    public ArrayList<SessionCardResult> getResults() {
        return results;
    }
}
