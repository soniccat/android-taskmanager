package learning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class CardTeacher {

    private int cardPerSession = 7;

    private List<Card> cards = new ArrayList<>();
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

    public CardTeacher(UUID courseId) {
        Course course = getCourseHolder().getCourse(courseId);
        cards.addAll(course.getCards());
        buildCourseSession();
    }

    public CardTeacher(List<Card> cards) {
        this.cards.addAll(cards);
        buildCourseSession();
    }

    private void buildCourseSession() {
        ArrayList<Card> sessinCards = new ArrayList<>();
        sessinCards.addAll(this.cards);
        Collections.shuffle(sessinCards);

        ArrayList<Card> answeredCards = new ArrayList<>();
        for (LearnSession session : sessions) {
            answeredCards.addAll(session.getRightAnsweredCards());
        }

        for (Card card : answeredCards) {
            int index = sessinCards.indexOf(card);
            if (index != -1) {
                sessinCards.remove(index);
            }
        }

        if (sessinCards.size() > cardPerSession) {
            sessinCards.subList(cardPerSession-1, sessinCards.size()-1).clear();
        }

        currentSession = new LearnSession(sessinCards);
    }

    public Card getCurrentCard() {
        return currentSession != null ? currentSession.getCurrentCard() : null;
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
        if (checkCount > 2) {
            countWronAnswer();
        }
    }

    public void onHintShown() {
        hintShowCount++;

        if (hintShowCount > 1) {
            countWronAnswer();
        }
    }

    public void onSessionsFinished() {
        sessions.add(currentSession);
        buildCourseSession();
    }

    public LearnSession getCurrentSession() {
        return currentSession;
    }

    private void countWronAnswer() {
        if (!isWrongAnswerCounted) {
            getCourseHolder().countWrongAnswer(getCurrentCard());
            currentSession.updateProgress(getCurrentCard(), false);
            isWrongAnswerCounted = true;
        }
    }

    private void countRightAnswer() {
        getCourseHolder().countRighAnswer(getCurrentCard());
        currentSession.updateProgress(getCurrentCard(), true);
    }


    public boolean isWrongAnswerCounted() {
        return isWrongAnswerCounted;
    }
}
