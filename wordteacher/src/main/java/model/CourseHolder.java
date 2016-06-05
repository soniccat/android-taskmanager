package model;

import com.example.alexeyglushkov.cachemanager.CacheEntry;
import com.example.alexeyglushkov.cachemanager.DiskCacheEntry;
import com.example.alexeyglushkov.cachemanager.DiskCacheProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import main.MainApplication;

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
        Error error = null;
        if (course != null) {
            error = storeCourse(course);
            if (error == null) {
                courses.add(course);
            }
        }

        return error;
    }

    public Course getCourse(UUID courseId) {
        Course course = null;
        for (Course c : getCourses()) {
            if (c.getId().equals(courseId)) {
                course = c;
                break;
            }
        }

        return course;
    }

    public Card getCard(UUID cardId) {
        Card resultCard = null;
        for (Course course : getCourses()) {
            for (Card card : course.getCards()) {
                if (card.getId().equals(cardId)) {
                    resultCard = card;
                    break;
                }
            }

            if (resultCard != null) {
                break;
            }
        }

        return resultCard;
    }

    private Error storeCourse(Course course) {
        Error error = diskProvider.put(course.getId().toString(), course, null);
        return error;
    }

    public Error removeCourse(UUID courseId) {
        Course course = getCourse(courseId);
        return removeCourse(course);
    }

    public Error removeCourse(Course course) {
        Error error = diskProvider.remove(course.getId().toString());
        if (error == null) {
            courses.remove(course);
        }

        return error;
    }

    public boolean removeCard(Card card) {
        boolean isRemoved = false;
        Course course = getCourse(card.getCourseId());
        if (course != null) {
            int index = course.getCardIndex(card);
            if (index != -1) {
                course.removeCard(card);

                Error error = storeCourse(course);
                if (error != null) {
                    course.addCard(index, card);
                } else {
                    isRemoved = true;
                }
            }
        }

        return isRemoved;
    }

    public void countRighAnswer(Card card) {
        Course course = getCourse(card.getCourseId());
        countRighAnswer(course, card);
    }

    public void countRighAnswer(Course course, Card card) {
        CardProgress progress = card.getProgress();
        if (progress == null) {
            progress = new CardProgress();
            card.setProgress(progress);
        }

        progress.countRightAnswer();
        storeCourse(course);
    }

    public void countWrongAnswer(Card card) {
        Course course = getCourse(card.getCourseId());
        countWrongAnswer(course, card);
    }

    public void countWrongAnswer(Course course, Card card) {
        CardProgress progress = card.getProgress();
        if (progress != null) {
            progress.countWrongAnswer();
            storeCourse(course);
        }
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }
}
