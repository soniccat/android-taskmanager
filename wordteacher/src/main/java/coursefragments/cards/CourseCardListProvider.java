package coursefragments.cards;

import android.os.Bundle;

import java.util.List;
import java.util.UUID;

import listfragment.SimpleStorableListProvider;
import listfragment.StorableListProvider;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CourseCardListProvider implements StorableListProvider<Card> {
    public final static String ARG_PARENT_COURSE_ID = "ARG_PARENT_COURSE_ID";

    private Course course;

    //// Initialization
    public CourseCardListProvider(Course course) {
        this.course = course;
    }

    //// Interface methods

    // StorableListProvider

    @Override
    public void store(Bundle bundle) {
        bundle.putString(ARG_PARENT_COURSE_ID, course.getId().toString());
    }

    static boolean canRestore(Bundle bundle) {
        return bundle != null && bundle.containsKey(ARG_PARENT_COURSE_ID);
    }

    @Override
    public void restore(Bundle bundle, Object context) {
        String strId = bundle.getString(ARG_PARENT_COURSE_ID);
        course = getCourse((CourseHolder) context, strId);
    }

    //// Getters

    private Course getCourse(CourseHolder context, String strId) {
        CourseHolder holder = context;
        return holder.getCourse(UUID.fromString(strId));
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public List<Card> getList() {
        return course.getCards();
    }
}
