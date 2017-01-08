package learning;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import courselistmodules.cardlistmodule.presenter.CardListProvider;
import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;
import tools.BundleTools;

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

    //// Initialization

    public CardTeacher(Bundle bundle, CourseHolder holder) {
        restore(bundle, holder);
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

    public void restore(Bundle bundle, CourseHolder holder) {
        cardPerSession = bundle.getInt("cardPerSession");
        CardListProvider cardListProvider = new CardListProvider(bundle, holder);
        cards = cardListProvider.getList();

        restoreSessions(bundle, holder);
        currentSession = new LearnSession(bundle, holder);
        checkCount = bundle.getInt("checkCount");
        hintShowCount = bundle.getInt("hintShowCount");
        isWrongAnswerCounted = bundle.getBoolean("isWrongAnswerCounted");
    }

    private void restoreSessions(Bundle bundle, CourseHolder holder) {
        sessions = new ArrayList<>();
        List<Bundle> bundles = BundleTools.restoreBundles(bundle, "sessions");
        for (Bundle b : bundles) {
            sessions.add(new LearnSession(b, holder));
        }
    }

    public void store(Bundle bundle) {
        bundle.putInt("cardPerSession", cardPerSession);

        CardListProvider cardListProvider = new CardListProvider(cards);
        cardListProvider.store(bundle);

        storeSessions(bundle);
        currentSession.store(bundle);
        bundle.putInt("checkCount", checkCount);
        bundle.putInt("hintShowCount", hintShowCount);
        bundle.putBoolean("isWrongAnswerCounted", isWrongAnswerCounted);
    }

    private void storeSessions(Bundle bundle) {
        List<Bundle> sessionBundles = new ArrayList<>();
        for (LearnSession session : sessions) {
            Bundle b = new Bundle();
            session.store(b);
            sessionBundles.add(b);
        }

        BundleTools.storeBundles(bundle, "sessions", sessionBundles);
    }

    //// Actions

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

    private void prepareToNewCard() {
        isWrongAnswerCounted = false;
        checkCount = 0;
        hintShowCount = 0;
    }

    public void onCheckInput() {
        ++checkCount;
    }

    public void onRightInput() throws Exception {
        countRightAnswer();
    }

    public void onGiveUp() throws Exception {
        countWronAnswer();
    }

    public void onWrongInput() throws Exception {
        if (checkCount > 1) {
            countWronAnswer();
        }
    }

    public void onHintShown() throws Exception {
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

    private void countWronAnswer() throws Exception {
        if (!isWrongAnswerCounted) {
            getCourseHolder().countWrongAnswer(getCurrentCard());
            currentSession.updateProgress(getCurrentCard(), false);
            isWrongAnswerCounted = true;
        }
    }

    private void countRightAnswer() throws Exception {
        getCourseHolder().countRighAnswer(getCurrentCard());
        currentSession.updateProgress(getCurrentCard(), true);
    }

    public boolean isWrongAnswerCounted() {
        return isWrongAnswerCounted;
    }

    //// Getters

    public Card getCurrentCard() {
        return currentSession != null ? currentSession.getCurrentCard() : null;
    }

    public Card getNextCard() {
        prepareToNewCard();
        return currentSession.getNextCard();
    }

    // Cast Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

}
