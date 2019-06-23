package com.example.alexeyglushkov.wordteacher.model;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.aglushkov.repository.RepositoryCommandHolder;
import com.aglushkov.repository.command.DisposableRepositoryCommand;
import com.aglushkov.repository.command.RepositoryCommand;
import com.aglushkov.repository.livedata.NonNullMutableLiveData;
import com.aglushkov.repository.livedata.Resource;
import com.example.alexeyglushkov.cachemanager.StorageEntry;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorageEntry;
import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
import com.example.alexeyglushkov.taskmanager.task.SimpleTask;
import com.example.alexeyglushkov.taskmanager.task.Task;
import com.example.alexeyglushkov.taskmanager.task.TaskPool;
import com.example.alexeyglushkov.taskmanager.task.TaskProvider;
import com.example.alexeyglushkov.taskmanager.task.Tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class CourseHolder {
    // TODO: user async storage
    private @NonNull DiskStorage diskProvider;

    private final static long LOAD_COURSES_COMMAND_ID = 0;
    private RepositoryCommandHolder commandHolder = new RepositoryCommandHolder();

    private TaskProvider taskProvider;

    //// Initialization

    public CourseHolder(File directory, TaskProvider taskProvider) {
        this.taskProvider = taskProvider;

        diskProvider = new DiskStorage(directory);
        diskProvider.setDefaultCodec(new CourseCodec());
    }

    //// Actions

    public RepositoryCommand<Resource<List<Course>>> loadCourses(final ProgressListener progressListener) {
        Disposable disposable = loadCoursesAsync(progressListener).subscribe(Functions.emptyConsumer(), Functions.emptyConsumer());
        return commandHolder.putCommand(new DisposableRepositoryCommand<>(LOAD_COURSES_COMMAND_ID, disposable, getCoursesLiveData()));
    }

    private Single<List<Course>> loadCoursesAsync(ProgressListener progressListener) {
        Task task = createLoadCoursesTask(progressListener);
        Single<List<Course>> single = Tasks.toSingle(task, this.taskProvider);

        final NonNullMutableLiveData<Resource<List<Course>>> coursesLiveData = getCoursesLiveData();
        final Resource.State previousState = getCoursesLiveData().getValue().state;
        coursesLiveData.setValue(coursesLiveData.getValue().resource(Resource.State.Loading));

        return single.doOnSuccess(new Consumer<List<Course>>() {
            @Override
            public void accept(List<Course> courses) throws Exception {
                coursesLiveData.setValue(coursesLiveData.getValue().resource(Resource.State.Loaded, courses));
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                coursesLiveData.setValue(coursesLiveData.getValue().resource(previousState, throwable));
            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                coursesLiveData.setValue(coursesLiveData.getValue().resource(previousState));
            }
        });
    }

    private Task createLoadCoursesTask(final ProgressListener progressListener) {
        return new SimpleTask() {
            @Override
            public void startTask(Callback callback) {
                super.startTask(callback);

                ArrayList<Course> courses = null;
                try {
                    courses = loadCoursesSync(progressListener);
                    getPrivate().setTaskResult(courses);

                } catch (Exception e) {
                    getPrivate().setTaskError(new Error(e));
                }

                getPrivate().handleTaskCompletion(callback);
            }
        };
    }

    public ArrayList<Course> loadCoursesSync(final ProgressListener progressListener) throws Exception {
        ArrayList<Course> courses = new ArrayList<>();

        List<StorageEntry> entries = diskProvider.getEntries();
        for (StorageEntry entry : entries) {
            DiskStorageEntry diskEntry = (DiskStorageEntry)entry;
            Course course = (Course)diskEntry.getObject();
            courses.add(course);
        }

        return courses;
    }

    public void addCourse(Course course) throws Exception {
        storeCourse(course);
        // TODO: update live data
        //courses.add(course);
        //onCoursesAdded(Collections.singletonList(course));
    }

    public void addNewCards(Course course, List<Card> cards) throws Exception {
        // TODO: update live data
//        ArrayList<Card> cardsCopy = new ArrayList<>(course.getCards());
//        UpdateBatch updateBatch = new UpdateBatch();
//
//        ArrayList<Pair<Card, Card>> updatedCards = getUpdatedCards(course, cards);
//        for (Pair<Card, Card> pair : updatedCards) {
//            Card courseCard = pair.first;
//            Card newCard = pair.second;
//            boolean isChanged = updateCard(courseCard, newCard);
//            if (isChanged) {
//                updateBatch.updatedCards.add(courseCard);
//            }
//        }
//
//        ArrayList<Card> newCards = getNewCards(course, cards);
//        course.addCards(newCards);
//
//        updateBatch.addedCards.addAll(newCards);
//
//        try {
//            storeCourse(course);
//            onCourseUpdated(course, updateBatch);
//
//        } catch (Exception e) {
//            course.setCards(cardsCopy);
//            throw e;
//        }
    }

    private boolean updateCard(Card existingCard, Card newCard) {
        boolean isTermChanged = !existingCard.getTerm().equals(newCard.getTerm());
        boolean isDefinitionChanged = !existingCard.getDefinition().equals(newCard.getDefinition());
        boolean isChanged = isTermChanged || isDefinitionChanged;

        if (isChanged) {
            existingCard.setTerm(newCard.getTerm());
            existingCard.setDefinition(newCard.getDefinition());
            existingCard.setProgress(null);
        }

        return isChanged;
    }

    private void storeCourse(Course course) throws Exception {
        diskProvider.put(getKey(course), course, null);
    }

    public void removeCourse(UUID courseId) throws Exception {
        // TODO: update live data
//        Course course = getCourse(courseId);
//        removeCourse(course);
    }

    public void removeCourse(Course course) throws Exception {
        // TODO: update live data
        diskProvider.remove(getKey(course));
//        courses.remove(course);
//        onCoursesDeleted(Collections.singletonList(course));
    }

    public void removeCard(Card card) throws Exception {
        // TODO: update live data
//        Course course = getCourse(card.getCourseId());
//        if (course != null) {
//            int index = course.getCardIndex(card);
//            if (index != -1) {
//                course.removeCard(card);
//
//                try {
//                    storeCourse(course);
//
//                    UpdateBatch batch = new UpdateBatch();
//                    batch.removedCards.add(card);
//                    onCourseUpdated(course, batch);
//
//                } catch (Exception e) {
//                    // rollback
//                    course.addCard(index, card);
//                    throw e;
//                }
//            }
//        }
    }

    public void countRighAnswer(Card card) throws Exception {
//        Course course = getCourse(card.getCourseId());
//        countRighAnswer(course, card);
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
        // TODO: write implementation
//        Course course = getCourse(card.getCourseId());
//        countWrongAnswer(course, card);
    }

    public void countWrongAnswer(Course course, Card card) throws Exception {
        CardProgress progress = card.getProgress();
        if (progress != null) {
            progress.countWrongAnswer();
            storeCourse(course);
        }
    }

    //// Getters

    @NonNull
    public NonNullMutableLiveData<Resource<List<Course>>> getCoursesLiveData() {
        NonNullMutableLiveData<Resource<List<Course>>> liveData = commandHolder.getLiveData(LOAD_COURSES_COMMAND_ID);
        if (liveData == null) {
            liveData = new NonNullMutableLiveData<>(new Resource<List<Course>>());
            commandHolder.putLiveData(LOAD_COURSES_COMMAND_ID, liveData);
        }
        return liveData;
    }

    @NonNull
    public File getDirectory() {
        return diskProvider.getDirectory();
    }

//    @NonNull
//    public ArrayList<Course> getCourses() {
//        return courses;
//    }

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
                result.add(new Pair<>(courseCard, cards.get(i)));
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
        List<Course> courses = getCoursesLiveData().getValue().data;
        if (courses != null) {
            for (Course c : courses) {
                if (c.getId().equals(courseId)) {
                    course = c;
                    break;
                }
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
        List<Course> courses = getCoursesLiveData().getValue().data;
        if (courses != null) {
            for (Course course : courses) {
                resultCard = getCard(cardId, course.getCards());

                if (resultCard != null) {
                    break;
                }
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

    //// Interfaces
//
//    public interface CourseHolderListener {
//        void onLoaded(@NonNull CourseHolder holder);
//        void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses);
//        void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses);
//        void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull UpdateBatch batch);
//    }
//
//    public class UpdateBatch {
//        public @NonNull List<Card> removedCards = new ArrayList<>();
//        public @NonNull List<Card> addedCards = new ArrayList<>();
//        public @NonNull List<Card> updatedCards = new ArrayList<>();
//    }
}
