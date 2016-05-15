package learning;

import java.util.UUID;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class CourseTeacher {

    private Course course;
    private int currentIndex = 0;

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
    }

    public Card getCurrentCard() {
        return course.getCards().get(currentIndex);
    }

    public Card getNextCard() {
        prepareToNewCard();

        Card result = null;
        ++currentIndex;
        if (currentIndex < course.getCards().size()) {
            result = course.getCards().get(currentIndex);
        }

        return result;
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
