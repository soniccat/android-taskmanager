package com.example.alexeyglushkov.quizletservice.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexeyglushkov on 02.04.16.
 */
public class QuizletTerm implements Parcelable {
    private long id;
    private String term;
    private String definition;
    private int rank;

    public QuizletTerm() {

    }

    public QuizletTerm(Parcel parcel) {
        Bundle bundle = parcel.readBundle();
        id = bundle.getLong("id");
        term = bundle.getString("term");
        definition = bundle.getString("definition");
        rank = bundle.getInt("rank");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("term", term);
        bundle.putString("definition", definition);
        bundle.putInt("rank", rank);

        parcel.writeBundle(bundle);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public static final Parcelable.Creator<QuizletTerm> CREATOR = new Parcelable.Creator<QuizletTerm>() {
        public QuizletTerm createFromParcel(Parcel in) {
            return new QuizletTerm(in);
        }

        public QuizletTerm[] newArray(int size) {
            return new QuizletTerm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
