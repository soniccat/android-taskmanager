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
        Bundle bundle = parcel.readBundle();
        id = UUID.fromString(bundle.getString("id"));
        title = bundle.getString("title");
        createDate = new Date(bundle.getLong("createDate"));
        cards = bundle.getParcelableArrayList("cards");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id.toString());
        bundle.putString("title", title);
        bundle.putLong("createDate", createDate.getTime());
        bundle.putParcelableArrayList("cards", cards);

        parcel.writeBundle(bundle);
    }

    public Course() {
        id = UUID.randomUUID();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public UUID getId() {
        return id;
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
