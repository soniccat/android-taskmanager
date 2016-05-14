package model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexeyglushkov on 14.05.16.
 */
public class CardProgress {

    private int MIN = 60;
    private int HOUR = 25*MIN;
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

    public float getProgress() {
        float result = 0;
        if (rightAnswerCount < LEARN_TABLE.length) {
            result = getProgress(rightAnswerCount);
        } else {
            result = 100;
        }

        return result;
    }

    public boolean needHaveLesson() {
        boolean result = false;
        if (lastLessonDate != null) {
            result = new Date().compareTo(lastLessonDate) >= 0;
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

            if (lastMistakeCount >= 2 || rightAnswerCount > 4) {
                lastMistakeCount = 0;
                rightAnswerCount--;
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

    /*
    public class CardLesson {
        public Date date;
        public int numberOfChecks;
        boolean isNextLetterHintUsed;
        boolean isRandomLetterHintUsed;
        boolean definitionToTerm;

        public CardLesson() {
            date = new Date();
        }
    }*/
}
