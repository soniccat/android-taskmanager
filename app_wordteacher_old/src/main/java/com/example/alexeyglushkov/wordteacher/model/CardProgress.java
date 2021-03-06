package com.example.alexeyglushkov.wordteacher.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgress implements Parcelable {

    // TODO: move it in tool lib
    private int MIN = 60000;
    private int HOUR = 60*MIN;
    private int LEARN_SPAN = 8*HOUR;

    // progress, next lesson time interval
    private int[][] LEARN_TABLE = new int[][] {
            { 0,     0},
            { 5,     0},
            {20, LEARN_SPAN},
            {30, LEARN_SPAN},
            {50, 2* LEARN_SPAN},
            {70, 2* LEARN_SPAN},
            {90, 3* LEARN_SPAN}
    };

    private final int lastLevel = 7;

    //private ArrayList<CardLesson> rightLessons = new ArrayList<>();
    private int rightAnswerCount = 0;
    private int lastMistakeCount = 0;
    private Date lastLessonDate;

    public CardProgress(Parcel parcel) {
        rightAnswerCount = parcel.readInt();
        lastMistakeCount = parcel.readInt();
        long time = parcel.readLong();
        lastLessonDate = new Date(time);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rightAnswerCount);
        dest.writeInt(lastMistakeCount);
        dest.writeLong(lastLessonDate.getTime());
    }

    public CardProgress() {

    }

    public float getProgress() {
        float result = 0;
        if (rightAnswerCount < LEARN_TABLE.length) {
            result = getProgress(rightAnswerCount);
        } else {
            result = 100;
        }

        return result / 100.0f;
    }

    public boolean needHaveLesson() {
        boolean result = true;
        if (isCompleted()) {
            result = false;
        } else {
            Date newLessonDate = getNextLessonDate();
            if (newLessonDate != null) {
                result = new Date().compareTo(newLessonDate) >= 0;
            }
        }

        return result;
    }

    public Date getNextLessonDate() {
        Date result = null;
        if (lastLessonDate != null) {
            int interval = getNextLessonInterval(rightAnswerCount);
            result = new Date(lastLessonDate.getTime() + interval);
        }

        return result;
    }

    public boolean isCompleted() {
        return rightAnswerCount >= lastLevel;
    }

    public void countRightAnswer() {
        if (needHaveLesson()) {
            ++rightAnswerCount;
            lastMistakeCount = 0;
            updateLastLessonDate();
        }
    }

    public void countWrongAnswer() {
        if (needHaveLesson()) {
            ++lastMistakeCount;

            if (lastMistakeCount >= 2) {
                lastMistakeCount = 0;

                if (rightAnswerCount > 0) {
                    rightAnswerCount--;
                }
            }

            updateLastLessonDate();
        }
    }

    private int getProgress(int position) {
        return LEARN_TABLE[position][0];
    }

    private int getNextLessonInterval(int position) {
        return LEARN_TABLE[position][1];
    }

    private void updateLastLessonDate() {
        lastLessonDate = new Date();

        long time = lastLessonDate.getTime();
        Date date = new Date(time);
        int i=0;
        ++i;
    }

    public static final Parcelable.Creator<CardProgress> CREATOR = new Parcelable.Creator<CardProgress>() {
        public CardProgress createFromParcel(Parcel in) {
            return new CardProgress(in);
        }

        public CardProgress[] newArray(int size) {
            return new CardProgress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // for parsers

    public int getRightAnswerCount() {
        return rightAnswerCount;
    }

    public void setRightAnswerCount(int rightAnswerCount) {
        this.rightAnswerCount = rightAnswerCount;
    }

    public int getLastMistakeCount() {
        return lastMistakeCount;
    }

    public void setLastMistakeCount(int lastMistakeCount) {
        this.lastMistakeCount = lastMistakeCount;
    }

    public Date getLastLessonDate() {
        return lastLessonDate;
    }

    public void setLastLessonDate(Date lastLessonDate) {
        this.lastLessonDate = lastLessonDate;
    }
}
