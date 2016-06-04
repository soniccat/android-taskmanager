package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class Course implements Parcelable {
    private UUID id;
    private Date createDate;
    private String title;
    private ArrayList<Card> cards = new ArrayList<>();

    public Course(Parcel parcel) {
        //TODO: remove bundle
        Bundle bundle = parcel.readBundle(Card.class.getClassLoader());
        id = UUID.fromString(bundle.getString("id"));
        title = bundle.getString("title");
        createDate = new Date(bundle.getLong("createDate"));
        cards = bundle.getParcelableArrayList("cards");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        //TODO: remove bundle
        Bundle bundle = new Bundle();
        bundle.putString("id", id.toString());
        bundle.putString("title", title);
        bundle.putLong("createDate", createDate.getTime());
        bundle.putParcelableArrayList("cards", cards);

        parcel.writeBundle(bundle);
    }

    public Course() {
        id = UUID.randomUUID();
        createDate = new Date();
    }

    public List<Card> getInProgressCards() {
        List<Card> result = new ArrayList<>();

        for (Card card : cards) {
            if (card.getProgress() != null) {
                result.add(card);
            }
        }

        return result;
    }

    public List<Card> getReadyToLearnCards() {
        List<Card> result = new ArrayList<>();

        for (Card card : cards) {
            if (card.getProgress() != null) {
                CardProgress progress = card.getProgress();
                if (progress.needHaveLesson()) {
                    result.add(card);
                }
            }
        }

        return result;
    }

    public Card getCard(UUID courseId) {
        Card card = null;
        for (Card c : getCards()) {
            if (c.getId().equals(courseId)) {
                card = c;
                break;
            }
        }

        return card;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Course card = (Course)o;
            return id.equals(card.id);
        }

        return false;
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
