package model;

import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageEntry;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageProvider;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.WeakRefList;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class CourseHolder {
    public enum State {
        Unitialized,
        Loaded
    }

    private DiskStorageProvider diskProvider;
    private ArrayList<Course> courses = new ArrayList<>();
    private WeakRefList<CourseHolderListener> listeners = new WeakRefList<>();

    private State state = State.Unitialized;

    //// Initialization

    public CourseHolder(File directory) {
        diskProvider = new DiskStorageProvider(directory);
        diskProvider.setDefaultSerializer(new CourseSerializer());
    }

    //// Events

    private void onLoaded() {
        for (WeakReference<CourseHolderListener> listener : listeners) {
            listener.get().onLoaded();
        }
    }

    //// Actions

    // Listeners

    public void addListener(CourseHolderListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(new WeakReference<>(listener));
        }
    }

    public void removeListener(CourseHolderListener listener) {
        listeners.remove(listener);
    }

    //

    public ArrayList<Course> loadCourses() {
        ArrayList<Course> courses = new ArrayList<>();

        List<StorageEntry> entries = diskProvider.getEntries();
        for (StorageEntry entry : entries) {
            DiskStorageEntry diskEntry = (DiskStorageEntry)entry;
            Course course = (Course)diskEntry.getObject();
            if (course != null) {
                courses.add(course);
            }
        }

        return courses;
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

        try {
            storeCourse(course);

        } catch (Exception e) {
            course.setCards(cardsCopy);
            isAdded = false;
        }

        return isAdded;
    }

    private void storeCourse(Course course) throws Exception {
        diskProvider.put(getKey(course), course, null);
    }

    public Error removeCourse(UUID courseId) {
        Course course = getCourse(courseId);
        return removeCourse(course);
    }

    public Error removeCourse(Course course) {
        Error error = diskProvider.remove(getKey(course));
        if (error == null) {
            courses.remove(course);
        }

        return error;
    }

    public Error removeCard(Card card) {
        Error error = null;
        Course course = getCourse(card.getCourseId());
        if (course != null) {
            int index = course.getCardIndex(card);
            if (index != -1) {
                course.removeCard(card);

                error = storeCourse(course);
                if (error != null) {
                    // rollback
                    course.addCard(index, card);
                }
            }
        }

        return error;
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

    //// Getters

    public Task getLoadCourseListTask() {
        final Task task = new SimpleTask() {
            @Override
            public void startTask() {
                ArrayList<Course> courses = loadCourses();
                getPrivate().setTaskResult(courses);
                handleTaskCompletion();
            }
        };

        task.setTaskCallback(new Task.Callback() {
            @Override
            public void onCompleted(boolean cancelled) {
                Object res = task.getTaskResult();
                if (res != null) {
                    courses = (ArrayList<Course>) res;
                }

                state = State.Loaded;
                onLoaded();
            }
        });

        return task;
    }

    public File getDirectory() {
        return diskProvider.getDirectory();
    }

    public ArrayList<Course> getCourses() {
        return courses;
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

    public File getCourseFile(Course course) {
        return diskProvider.getKeyFile(getKey(course));
    }

    private String getKey(Course course) {
        return course.getId().toString();
    }

    // TODO: optimize
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

    public State getState() {
        return state;
    }

    //// Interfaces

    public interface CourseHolderListener {
        void onLoaded();
    }
}
