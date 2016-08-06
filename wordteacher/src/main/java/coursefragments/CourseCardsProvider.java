package coursefragments;

import java.util.List;

import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 06.08.16.
 */
public class CourseCardsProvider implements CardListProvider {
    private Course course;

    public CourseCardsProvider(Course course) {
        this.course = course;
    }

    @Override
    public List<Card> getCards() {
        return course.getCards();
    }

    public Course getCourse() {
        return course;
    }
}
