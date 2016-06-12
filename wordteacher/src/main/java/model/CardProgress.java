package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgress implements Parcelable {

    // TODO: move it in tool lib
    private int MIN = 60000;
    private int HOUR = 60*MIN;
    private int DAY = 24*HOUR;

    // progress, next lesson time interval
    private int[][] LEARN_TABLE = new int[][] {
            { 0,     0},
            { 5,     0},
            {20,   DAY},
            {30,   DAY},
            {50, 2*DAY},
            {70, 2*DAY},
            {90, 3*DAY}
    };

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
        if (lastLessonDate != null) {
            int interval = getNextLessonInterval(rightAnswerCount);
            Date newLessonDate = new Date(lastLessonDate.getTime() + interval);
            result = new Date().compareTo(newLessonDate) >= 0;
        }

        return result;
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
