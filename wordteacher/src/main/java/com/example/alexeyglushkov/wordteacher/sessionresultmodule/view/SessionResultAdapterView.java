package com.example.alexeyglushkov.wordteacher.sessionresultmodule.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

/**
 * Created by alexeyglushkov on 01.04.17.
 */

public class SessionResultAdapterView implements Parcelable {
    public SessionCardResult result;
    public Card card;

    public SessionResultAdapterView() {
    }

    public SessionResultAdapterView(Parcel p) {
        result = p.readParcelable(getClass().getClassLoader());
        card = p.readParcelable(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
        dest.writeParcelable(card, flags);
    }

    public static final Parcelable.Creator<SessionResultAdapterView> CREATOR = new Parcelable.Creator<SessionResultAdapterView>() {
        public SessionResultAdapterView createFromParcel(Parcel in) {
            return new SessionResultAdapterView(in);
        }

        public SessionResultAdapterView[] newArray(int size) {
            return new SessionResultAdapterView[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
