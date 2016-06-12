package model;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.DiskStorageEntry;
import com.example.alexeyglushkov.cachemanager.DiskStorageProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class CourseHolder {
    private DiskStorageProvider diskProvider;
    private ArrayList<Course> courses = new ArrayList<>();

    public CourseHolder(File directory) {
        diskProvider = new DiskStorageProvider(directory);
        diskProvider.setSerializer(new CourseSerializer(), Course.class);
    }

    public void loadCourses() {
        List<StorageEntry> entries = diskProvider.getEntries();
        for (StorageEntry entry : entries) {
            DiskStorageEntry diskEntry = (DiskStorageEntry)entry;
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

    public boolean addNewCards(Course course, List<Card> cards) {
        boolean isAdded = true;
        ArrayList<Card> cardsCopy = new ArrayList<>(course.getCards());

        ArrayList<Card> newCards = getNewCards(course, cards);
        course.addCards(newCards);
        Error error = storeCourse(course);
        if (error != null) {
            course.setCards(cardsCopy);
            isAdded = false;
        }

        return isAdded;
    }

    public ArrayList<Card> getNewCards(Course course, List<Card> cards) {
        ArrayList<Card> result = new ArrayList<>(cards);
        List<Card> courseCards = course.getCards();
        for (Card courseCard : courseCards) {
            int i = getCardIndex(courseCard.getTerm(), result);
            if (i != -1) {
                result.remove(i);
            }
        }

        return result;
    }

    public int getCardIndex(String title, List<Card> cards) {
        int resultIndex = -1;
        for (int i=0; i<cards.size(); ++i) {
            if (cards.get(i).getTerm().equals(title)) {
                resultIndex = i;
                break;
            }
        }

        return resultIndex;
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
                    // rollback
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
