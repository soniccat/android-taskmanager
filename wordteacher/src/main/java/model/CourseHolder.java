package model;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

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

    private @NonNull DiskStorageProvider diskProvider;
    private @NonNull ArrayList<Course> courses = new ArrayList<>();
    private @NonNull WeakRefList<CourseHolderListener> listeners = new WeakRefList<>();

    private State state = State.Unitialized;

    //// Initialization

    public CourseHolder(File directory) {
        diskProvider = new DiskStorageProvider(directory);
        diskProvider.setDefaultSerializer(new CourseSerializer());
    }

    //// Events

    private void onLoaded() {
        for (WeakReference<CourseHolderListener> listener : listeners) {
            listener.get().onLoaded(this);
        }
    }

    private void onCourseDeleted(Course course) {
        for (WeakReference<CourseHolderListener> listener : listeners) {
            listener.get().onCourseRemoved(this, course);
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

            try {
                Course course = (Course)diskEntry.getObject();
                courses.add(course);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return courses;
    }

    public void addCourse(Course course) throws Exception {
        storeCourse(course);
        courses.add(course);
    }

    public void addNewCards(Course course, List<Card> cards) throws Exception {
        ArrayList<Card> cardsCopy = new ArrayList<>(course.getCards());
        ArrayList<Card> changedCards = new ArrayList<>();

        ArrayList<Pair<Card, Card>> updatedCards = getUpdatedCards(course, cards);
        for (Pair<Card, Card> pair : updatedCards) {
            Card newCard = pair.first;
            Card courseCard = pair.second;
            boolean isChanged = updateCard(courseCard, newCard);
            if (isChanged) {
                changedCards.add(courseCard);
            }
        }

        ArrayList<Card> newCards = getNewCards(course, cards);
        course.addCards(newCards);

        try {
            storeCourse(course);

        } catch (Exception e) {
            course.setCards(cardsCopy);
            throw e;
        }
    }

    private boolean updateCard(Card existingCard, Card newCard) {
        boolean isTermChanged = !existingCard.getTerm().equals(newCard.getTerm());
        boolean isDefinitionChanged = !existingCard.getDefinition().equals(newCard.getDefinition());

        if (isTermChanged || isDefinitionChanged) {
            existingCard.setTerm(newCard.getTerm());
            existingCard.setDefinition(newCard.getDefinition());
            existingCard.setProgress(null);
            return true;
        }

        return false;
    }

    private void storeCourse(Course course) throws Exception {
        diskProvider.put(getKey(course), course, null);
    }

    public void removeCourse(UUID courseId) throws Exception {
        Course course = getCourse(courseId);
        removeCourse(course);
    }

    public void removeCourse(Course course) throws Exception {
        diskProvider.remove(getKey(course));
        courses.remove(course);
        onCourseDeleted(course);
    }

    public void removeCard(Card card) throws Exception {
        Course course = getCourse(card.getCourseId());
        if (course != null) {
            int index = course.getCardIndex(card);
            if (index != -1) {
                course.removeCard(card);

                try {
                    storeCourse(course);

                } catch (Exception e) {
                    // rollback
                    course.addCard(index, card);
                    throw e;
                }
            }
        }
    }

    public void countRighAnswer(Card card) throws Exception {
        Course course = getCourse(card.getCourseId());
        countRighAnswer(course, card);
    }

    public void countRighAnswer(Course course, Card card) throws Exception {
        CardProgress progress = card.getProgress();
        if (progress == null) {
            progress = new CardProgress();
            card.setProgress(progress);
        }

        progress.countRightAnswer();
        storeCourse(course);
    }

    public void countWrongAnswer(Card card) throws Exception {
        Course course = getCourse(card.getCourseId());
        countWrongAnswer(course, card);
    }

    public void countWrongAnswer(Course course, Card card) throws Exception {
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

    @NonNull
    public File getDirectory() {
        return diskProvider.getDirectory();
    }

    @NonNull
    public ArrayList<Course> getCourses() {
        return courses;
    }

    private ArrayList<Card> getNewCards(Course course, List<Card> cards) {
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

    private ArrayList<Pair<Card, Card>> getUpdatedCards(Course course, List<Card> cards) {
        ArrayList<Pair<Card, Card>> result = new ArrayList<>();
        List<Card> courseCards = course.getCards();
        for (Card courseCard : courseCards) {
            int i = getCardIndex(courseCard.getTerm(), cards);
            if (i != -1) {
                result.add(new Pair<Card, Card>(cards.get(i), courseCard));
            }
        }

        return result;
    }

    private int getCardIndex(String title, List<Card> cards) {
        int resultIndex = -1;
        // TODO: looks sad
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
            resultCard = getCard(cardId, course.getCards());

            if (resultCard != null) {
                break;
            }
        }

        return resultCard;
    }

    private Card getCard(UUID cardId, List<Card> cards) {
        Card resultCard = null;
        for (Card card : cards) {
            if (card.getId().equals(cardId)) {
                resultCard = card;
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
        void onLoaded(CourseHolder holder);
        void onCourseRemoved(CourseHolder holder, Course course);
        //void onCourseUpdated(CourseHolder holder, Course course, List<Card> addedCards, List<Card> updatedCards, List<Card> removedCards);
    }
}
