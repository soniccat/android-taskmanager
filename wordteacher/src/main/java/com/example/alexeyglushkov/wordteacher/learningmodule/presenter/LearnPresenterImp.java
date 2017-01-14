package com.example.alexeyglushkov.wordteacher.learningmodule.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.learningmodule.view.LearnView;
import com.example.alexeyglushkov.wordteacher.learningmodule.SessionResultActivity;
import com.example.alexeyglushkov.wordteacher.main.MainApplication;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public class LearnPresenterImp implements LearnPresenter, CourseHolder.CourseHolderListener {
    public static final int ACTIVITY_RESULT = 10002;
    public static final int ACTIVITY_RESULT_CODE = 1;

    public final static String EXTRA_DEFINITION_TO_TERM = "EXTRA_DEFINITION_TO_TERM";
    public final static String EXTRA_CARD_IDS = "EXTRA_CARD_IDS";
    public final static char GAP_CHAR = '_';
    public final static String GAP_STRING = "_";

    private LearnView view;
    private @NonNull
    CardTeacher teacher;

    private boolean definitionToTerm;
    private StringBuilder hintArray = new StringBuilder();
    private @Nullable Bundle savedInstanceState;

    //// Initialization, restoration

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        view.setViewResult(ACTIVITY_RESULT_CODE);

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

    public void onDestroy() {
        getCourseHolder().removeListener(this);
    }

    public void onCheckPressed() {
        checkInput();
    }

    @Override
    public void onNextPressed() {
        showNextCard();
    }

    private void onRightInput() {
        try {
            teacher.onRightInput();
        } catch (Exception e) {
            view.showException(e);
        }

        showNextCard();
    }

    private void onWrongInput() {
        try {
            teacher.onWrongInput();
        } catch (Exception e) {
            view.showException(e);
        }

        view.showInputError(view.getContext().getResources().getString(R.string.error_wrong_input));
    }

    private void onSessionFinished() {
        LearnSession session = teacher.getCurrentSession();
        teacher.onSessionsFinished();

        showResultActivity(session);
    }

    public void onTextChanged() {
        if (isInputCorrect() && teacher.isWrongAnswerCounted()) {
            view.showNextButton();
        }
    }

    public void onShowNextLetterPressed() {
        int index = hintArray.indexOf(GAP_STRING);
        updateHintString(index);
        showHintString();
    }

    public void onShowRandomLetterPressed() {
        updateHintStringWithRandomLetter();
        showHintString();
    }

    public void onGiveUpPressed() {
        try {
            teacher.onGiveUp();
        } catch (Exception e) {
            view.showException(e);
        }

        view.setInputText("");

        String definition = getDefinition(teacher.getCurrentCard());
        for (int i=0; i<definition.length(); ++i) {
            hintArray.setCharAt(i, definition.charAt(i));
        }

        showHintString();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SessionResultActivity.ACTIVITY_RESULT) {
            handleResultActivityClose();
        }
    }

    //// Actions

    private void showCurrentCard() {
        prepareHintString();
        bindCurrentCard();
    }

    private void showNextCard() {
        teacher.getNextCard();

        if (teacher.getCurrentCard() != null) {
            bindCurrentCard();
        } else {
            onSessionFinished();
        }
    }

    private void bindCurrentCard() {
        Card card = teacher.getCurrentCard();
        view.bindCard(card, getTerm(card));
    }

    private void checkInput() {
        teacher.onCheckInput();

        if (isInputCorrect()) {
            onRightInput();
        } else {
            onWrongInput();
        }
    }

    private void showHintString() {
        try {
            teacher.onHintShown();
        } catch (Exception e) {
            view.showException(e);
        }

        view.showHintString(getHintString(), isHintStringFull());
    }

    // Hint

    private void prepareHintString() {
        hintArray.setLength(0);

        Card currentCard = teacher.getCurrentCard();
        for (int i = 0; i < getDefinition(currentCard).length(); ++i) {
            hintArray.append(GAP_CHAR);
        }
    }

    private void updateHintString(int index) {
        String definition = getDefinition(teacher.getCurrentCard());
        hintArray.setCharAt(index, definition.charAt(index));
    }

    private void updateHintStringWithRandomLetter() {
        int gapCount = getHintGapCount();
        int random = Math.abs(new Random(new Date().getTime()).nextInt());
        int gapIndex = random % gapCount;
        updateHintString(getGapIndexToCharIndex(gapIndex));
    }

    //// Activities

    // Result Activity

    private void showResultActivity(LearnSession session) {
        Intent intent = new Intent(view.getContext(), SessionResultActivity.class);
        intent.putExtra(SessionResultActivity.EXTERNAL_SESSION, session);
        view.startActivityForResult(intent, SessionResultActivity.ACTIVITY_RESULT);
    }

    private void handleResultActivityClose() {
        if (teacher.getCurrentCard() == null) {
            view.finish();

        } else {
            showCurrentCard();
            view.showInputFocus();
        }
    }

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

    private String getHintString() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i < hintArray.length(); ++i) {
            char ch = hintArray.charAt(i);
            builder.append(ch);

            if (builder.length() < hintArray.length()*2-1) {
                builder.append(' ');
            }
        }

        return builder.toString();
    }

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
        String input = view.getInputText();
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
