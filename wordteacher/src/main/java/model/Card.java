package model;

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
    private Date createDate;
    private String term;
    private String definition;

    private QuizletTerm quizletTerm;

    public Card(Parcel parcel) {
        Bundle bundle = parcel.readBundle();
        id = UUID.fromString(bundle.getString("id"));
        term = bundle.getString("term");
        definition = bundle.getString("definition");
        createDate = new Date(bundle.getLong("createDate"));
        quizletTerm = bundle.getParcelable("quizletTerm");
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id.toString());
        bundle.putString("term", term);
        bundle.putString("definition", definition);
        bundle.putLong("createDate", createDate.getTime());
        bundle.putParcelable("quizletTerm", quizletTerm);

        parcel.writeBundle(bundle);
    }

    public Card() {
        id = UUID.randomUUID();
        createDate = new Date();
    }

    public Date getCreateDate() {
        return createDate;
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
