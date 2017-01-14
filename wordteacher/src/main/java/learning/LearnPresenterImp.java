package learning;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import main.MainApplication;
import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public class LearnPresenterImp implements LearnPresenter, CourseHolder.CourseHolderListener {
    public final static String EXTRA_DEFINITION_TO_TERM = "EXTRA_DEFINITION_TO_TERM";
    public final static String EXTRA_CARD_IDS = "EXTRA_CARD_IDS";
    public final static char GAP_CHAR = '_';
    public final static String GAP_STRING = "_";

    private LearnView view;
    private @NonNull CardTeacher teacher;

    private boolean definitionToTerm;
    private StringBuilder hintArray = new StringBuilder();
    private @Nullable Bundle savedInstanceState;

    //// Initialization, restoration

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            createTeacher();
            definitionToTerm = view.getIntent().getBooleanExtra(EXTRA_DEFINITION_TO_TERM, false);
            onReady();

        } else {
            registerRestoration(savedInstanceState);
        }
    }

    private void registerRestoration(@Nullable final Bundle savedInstanceState) {
        final CourseHolder holder = getCourseHolder();
        if (holder.getState() == CourseHolder.State.Unitialized) {
            this.savedInstanceState = savedInstanceState;
            getCourseHolder().addListener(this);

        } else {
            restore(savedInstanceState);
        }
    }

    private void createTeacher() {
        String[] courseIdStrings = view.getIntent().getStringArrayExtra(EXTRA_CARD_IDS);
        List<Card> cards = new ArrayList<>();
        for (String id : courseIdStrings) {
            UUID cardId = UUID.fromString(id);
            Card card = getCourseHolder().getCard(cardId);
            cards.add(card);
        }

        teacher = new CardTeacher(cards);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        teacher.store(outState);
        outState.putBoolean("definitionToTerm", definitionToTerm);
        outState.putString("hintArray", hintArray.toString());
    }

    private void restore(Bundle savedInstanceState) {
        teacher = new CardTeacher(savedInstanceState, getCourseHolder());
        definitionToTerm = savedInstanceState.getBoolean("definitionToTerm");

        String string = savedInstanceState.getString("hintArray");
        hintArray = new StringBuilder(string);

        onRestore();
    }

    //// Events

    private void onReady() {
        showCurrentCard();
    }

    private void onRestore() {
        onReady();
    }

    //// Actions



    //// Interfaces

    // CourseHolder.CourseHolderListener

    @Override
    public void onLoaded(@NonNull CourseHolder holder) {
        restoreIfNeeded();
        holder.removeListener(this);
    }

    private void restoreIfNeeded() {
        if (savedInstanceState != null) {
            restore(savedInstanceState);
            savedInstanceState = null;
        }
    }

    @Override
    public void onCoursesAdded(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        // TODO: handle
    }

    @Override
    public void onCoursesRemoved(@NonNull CourseHolder holder, @NonNull List<Course> courses) {
        // TODO: handle
    }

    @Override
    public void onCourseUpdated(@NonNull CourseHolder holder, @NonNull Course course, @NonNull CourseHolder.UpdateBatch batch) {
        // TODO: handle
    }

    //// Setters

    public void setView(LearnView view) {
        this.view = view;
    }

    //// Getters

    private String getTerm(Card card) {
        return definitionToTerm ? card.getDefinition() : card.getTerm();
    }

    private String getDefinition(Card card) {
        return definitionToTerm ? card.getTerm() : card.getDefinition();
    }

    private int getHintGapCount() {
        int gapCount = 0;
        for (int i=0; i<hintArray.length(); ++i) {
            if (hintArray.charAt(i) == GAP_CHAR) {
                ++gapCount;
            }
        }
        return gapCount;
    }

    private int getGapIndexToCharIndex(int searchGapIndex) {
        int gapIndex = -1;
        int position = -1;
        for (int i=0; i<hintArray.length(); ++i) {
            if (hintArray.charAt(i) == GAP_CHAR) {
                ++gapIndex;
                if (gapIndex == searchGapIndex) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    // Statuses

    private boolean isHintStringFull() {
        return getHintGapCount() == 0;
    }

    private boolean isInputCorrect() {
        Card card = teacher.getCurrentCard();
        String input = inputLayout.getEditText().getText().toString();
        return input.equalsIgnoreCase(getDefinition(card));
    }

    // Cast Getters

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

}
