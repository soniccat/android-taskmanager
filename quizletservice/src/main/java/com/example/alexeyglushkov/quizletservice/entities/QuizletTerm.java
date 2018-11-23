package com.example.alexeyglushkov.quizletservice.entities;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 02.04.16.
 */
public class QuizletTerm implements Parcelable, Serializable {
    private static final long serialVersionUID = -6211744973878309135L;

    private long id;
    private long setId;
    private String term;
    private String definition;
    private int rank;

    public QuizletTerm() {
    }

    public QuizletTerm(Parcel parcel) {
        Bundle bundle = parcel.readBundle();
        id = bundle.getLong("id");
        setId = bundle.getLong("setId");
        term = bundle.getString("term");
        definition = bundle.getString("definition");
        rank = bundle.getInt("rank");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putLong("setId", setId);
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

    public long getSetId() {
        return setId;
    }

    public void setSetId(long setId) {
        this.setId = setId;
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
