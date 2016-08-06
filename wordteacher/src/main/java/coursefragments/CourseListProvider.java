package coursefragments;

import java.util.List;

import model.Course;

/**
 * Created by alexeyglushkov on 06.08.16.
 */
public interface CourseListProvider {
    List<Course> getCourses();
}
