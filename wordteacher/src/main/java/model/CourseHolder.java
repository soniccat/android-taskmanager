package model;

import com.example.alexeyglushkov.cachemanager.CacheEntry;
import com.example.alexeyglushkov.cachemanager.DiskCacheEntry;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class CourseHolder {
    private DiskCacheProvider diskProvider;

    private ArrayList<Course> courses = new ArrayList<>();

    public CourseHolder(File directory) {
        diskProvider = new DiskCacheProvider(directory);
        diskProvider.setSerializer(new CourseSerializer(), Course.class);
    }

    public void loadCourses() {
        List<CacheEntry> entries = diskProvider.getEntries();
        for (CacheEntry entry : entries) {
            DiskCacheEntry diskEntry = (DiskCacheEntry)entry;
            Course course = (Course)diskEntry.getObject();
            if (course != null) {
                courses.add(course);
            }
        }
    }

    public Error addCourse(Course course) {
        Error error = diskProvider.put(course.getId().toString(), course, null);
        if (error == null && course != null) {
            courses.add(course);
        }

        return error;
    }

    public Error removeCourse(Course course) {
        Error error = diskProvider.remove(course.getId().toString());
        if (error == null) {
            courses.remove(course);
        }

        return error;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }
}
