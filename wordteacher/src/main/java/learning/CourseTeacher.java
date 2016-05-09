package learning;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class CourseTeacher {

    private Course course;
    private int currentIndex = 0;

    public CourseTeacher(Course course) {
        this.course = course;
    }

    public Card getCurrentCard() {
        return course.getCards().get(currentIndex);
    }

    public Card getNextCard() {
        Card result = null;
        if (currentIndex < course.getCards().size()) {
            result = course.getCards().get(currentIndex);
            ++currentIndex;
        }

        return result;
    }
}
