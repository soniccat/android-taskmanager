package learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class CourseTeacher {

    private int cardPerSession = 7;

    private Course course;
    private ArrayList<LearnSession> sessions = new ArrayList<>();
    private LearnSession currentSession;

    private int checkCount;
    private int hintShowCount;
    private boolean isWrongAnswerCounted;

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    public CourseTeacher(UUID courseId) {
        this.course = getCourseHolder().getCourse(courseId);
        buildCourseSession();
    }

    public void buildCourseSession() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(course.getCards());

        ArrayList<Card> answeredCards = new ArrayList<>();
        for (LearnSession session : sessions) {
            answeredCards.addAll(session.getRightAnsweredCards());
        }

        for (Card card : answeredCards) {
            int index = cards.indexOf(card);
            if (index != -1) {
                cards.remove(index);
            }
        }

        if (cards.size() > cardPerSession) {
            cards.subList(cardPerSession, cards.size()-1).clear();
        }

        Collections.shuffle(cards);
        currentSession = new LearnSession(cards);
    }

    public Card getCurrentCard() {
        return currentSession.getCurrentCard();
    }

    public Card getNextCard() {
        prepareToNewCard();
        return currentSession.getNextCard();
    }

    private void prepareToNewCard() {
        isWrongAnswerCounted = false;
        checkCount = 0;
        hintShowCount = 0;
    }

    public void onCheckInput() {
        ++checkCount;
    }

    public void onRightInput() {
        countRightAnswer();
    }

    public void onGiveUp() {
        countWronAnswer();
    }

    public void onWrongInput() {
        if (checkCount > 1) {
            countWronAnswer();
        }
    }

    public void onHintShown() {
        hintShowCount++;

        if (hintShowCount > 1) {
            countWronAnswer();
        }
    }

    private void countWronAnswer() {
        if (!isWrongAnswerCounted) {
            getCourseHolder().countWrongAnswer(course, getCurrentCard());
            isWrongAnswerCounted = true;
        }
    }

    private void countRightAnswer() {
        getCourseHolder().countRighAnswer(course, getCurrentCard());
    }


    public boolean isWrongAnswerCounted() {
        return isWrongAnswerCounted;
    }
}
