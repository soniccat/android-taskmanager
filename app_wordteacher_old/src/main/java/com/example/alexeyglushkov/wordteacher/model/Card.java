package com.example.alexeyglushkov.wordteacher.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.Date;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class Card implements Parcelable {
    private UUID id;
    private UUID courseId;
    private Date createDate;
    private String term;
    private String definition;

    private CardProgress progress;
    private QuizletTerm quizletTerm;

    public Card(Parcel parcel) {
        Bundle bundle = parcel.readBundle(QuizletTerm.class.getClassLoader());
        id = UUID.fromString(bundle.getString("id"));
        courseId = UUID.fromString(bundle.getString("courseId"));
        term = bundle.getString("term");
        definition = bundle.getString("definition");
        createDate = new Date(bundle.getLong("createDate"));
        quizletTerm = bundle.getParcelable("quizletTerm");
        progress = bundle.getParcelable("progress");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id.toString());
        bundle.putString("courseId", courseId.toString());
        bundle.putString("term", term);
        bundle.putString("definition", definition);
        bundle.putLong("createDate", createDate.getTime());
        bundle.putParcelable("quizletTerm", quizletTerm);
        bundle.putParcelable("progress", progress);

        parcel.writeBundle(bundle);
    }

    public Card() {
        id = UUID.randomUUID();
        createDate = new Date();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public QuizletTerm getQuizletTerm() {
        return quizletTerm;
    }

    public void setQuizletTerm(QuizletTerm quizletTerm) {
        this.quizletTerm = quizletTerm;
    }

    public CardProgress getProgress() {
        return progress;
    }

    public float getFloatProgress() {
        return getProgress() != null ? getProgress().getProgress() : 0.0f;
    }

    public void setProgress(CardProgress progress) {
        this.progress = progress;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card card = (Card)o;
            return id.equals(card.id);
        }

        return false;
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        public Card createFromParcel(Parcel in) {
            return new Card(in);
        }

        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
