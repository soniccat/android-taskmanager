package com.example.alexeyglushkov.wordteacher.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

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

    public Course(Course course) {
        id = course.id;
        createDate = course.createDate;
        title = course.title;
        cards = new ArrayList<>(course.cards);
    }

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
            } else {
                result.add(card);
            }
        }

        return result;
    }

    public List<Card> getNotStartedCards() {
        List<Card> result = new ArrayList<>();

        for (Card card : cards) {
            CardProgress progress = card.getProgress();
            if (progress == null || progress.getProgress() == 0) {
                result.add(card);
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

    public int getCardIndex(Card card) {
        return cards.indexOf(card);
    }

    public void addCard(int index, Card card) {
        card.setCourseId(getId());
        cards.add(index, card);
    }

    public void addCard(Card card) {
        card.setCourseId(getId());
        cards.add(card);
    }

    public void addCards(List<Card> cards) {
        for (Card card : cards) {
            addCard(card);
        }
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
        this.cards = new ArrayList<>();
        for (Card card : cards) {
            addCard(card);
        }
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Course) {
            Course course = (Course)o;
            return id.equals(course.id);
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
