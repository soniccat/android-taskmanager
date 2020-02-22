package com.example.alexeyglushkov.wordteacher.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by alexeyglushkov on 15.05.16.
 */
public class SessionCardResult implements Parcelable {
    public UUID cardId;
    public float oldProgress;
    public float newProgress;
    public boolean isRight;

    public SessionCardResult() {

    }

    public SessionCardResult(Parcel parcel) {
        String stringId = parcel.readString();
        cardId = UUID.fromString(stringId);
        oldProgress = parcel.readFloat();
        newProgress = parcel.readFloat();
        isRight = parcel.readInt() > 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cardId.toString());
        dest.writeFloat(oldProgress);
        dest.writeFloat(newProgress);
        dest.writeInt(isRight ? 1 : 0);
    }

    public static final Parcelable.Creator<SessionCardResult> CREATOR
            = new Parcelable.Creator<SessionCardResult>() {
        public SessionCardResult createFromParcel(Parcel in) {
            return new SessionCardResult(in);
        }

        public SessionCardResult[] newArray(int size) {
            return new SessionCardResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}