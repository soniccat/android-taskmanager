package coursefragments;

import java.util.List;

import model.Course;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public class SimpleCourseListProvider implements CourseListProvider {
    List<Course> courses;

    public SimpleCourseListProvider(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public List<Course> getCourses() {
        return courses;
    }
}
