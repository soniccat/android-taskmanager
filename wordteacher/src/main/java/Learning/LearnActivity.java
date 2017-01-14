package learning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alexeyglushkov.tools.HandlerTools;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import main.BaseActivity;
import main.MainApplication;
import model.Card;
import model.CardProgress;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
// TODO: consider moving content to fragment
public class LearnActivity extends BaseActivity implements LearnView{

    public static final int ACTIVITY_RESULT = 10002;
    public static final int ACTIVITY_RESULT_CODE = 1;

    @NonNull LearnPresenter presenter;

    private View rootView;
    private TextView termView;
    private TextView progressTextView;
    private TextInputLayout inputLayout;
    private Button giveUpButton;
    private Button checkButton;
    private Button goNextButton;
    private ImageButton hintButton;

    //// Initialization

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_learn);
        bindViews();
        bindListeners();
        restore(savedInstanceState);

        setResult(ACTIVITY_RESULT_CODE, getIntent());
    }

    private void restore(Bundle savedInstanceState) {
        // because of the support lib bug
        String string = savedInstanceState.getString("input");
        inputLayout.getEditText().setText(string);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // because of the support lib bug
        outState.putString("input", inputLayout.getEditText().getText().toString());

        presenter.onSaveInstanceState(outState);
    }

    // Binding

    private void bindViews() {
        rootView = (View)findViewById(R.id.root);
        termView = (TextView)findViewById(R.id.word);
        progressTextView = (TextView)findViewById(R.id.progressTextView);
        inputLayout = (TextInputLayout)findViewById(R.id.definition);
        giveUpButton = (Button)findViewById(R.id.giveUpButton);
        checkButton = (Button)findViewById(R.id.checkButton);
        goNextButton = (Button)findViewById(R.id.go_next_button);
        hintButton = (ImageButton)findViewById(R.id.hit_button);
    }

    private void bindListeners() {
        giveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGiveUpPressed();
            }
        });
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });
        goNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextCard();
            }
        });
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintMenu();
            }
        });

        inputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LearnActivity.this.onTextChanged();
            }
        });

        inputLayout.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handed = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    checkInput();
                    handed = true;
                }

                return handed;
            }
        });
    }

    //// Events

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getCourseHolder().removeListener(this);
    }

    private void onRightInput() {
        try {
            teacher.onRightInput();
        } catch (Exception e) {
            showException(e);
        }

        showNextCard();
    }

    private void onWrongInput() {
        try {
            teacher.onWrongInput();
        } catch (Exception e) {
            showException(e);
        }

        inputLayout.setError(getString(R.string.error_wrong_input));
    }

    private void onSessionFinished() {
        LearnSession session = teacher.getCurrentSession();
        teacher.onSessionsFinished();

        showResultActivity(session);
    }

    private void onTextChanged() {
        if (isInputCorrect() && teacher.isWrongAnswerCounted()) {
            showNextButton();
        }
    }

    private void onShowNextLetterPressed() {
        int index = hintArray.indexOf(GAP_STRING);
        updateHintString(index);
        showHintString();
        updateHintButton();
    }

    private void onShowRandomLetterPressed() {
        updateHintStringWithRandomLetter();
        showHintString();
        updateHintButton();
    }

    private void onGiveUpPressed() {
        try {
            teacher.onGiveUp();
        } catch (Exception e) {
            showException(e);
        }

        inputLayout.getEditText().setText("");

        String definition = getDefinition(teacher.getCurrentCard());
        for (int i=0; i<definition.length(); ++i) {
            hintArray.setCharAt(i, definition.charAt(i));
        }

        showHintString();
        updateHintButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SessionResultActivity.ACTIVITY_RESULT) {
            handleResultActivityClose();
        }
    }

    //// Actions

    private void checkInput() {
        teacher.onCheckInput();

        if (isInputCorrect()) {
            onRightInput();
        } else {
            onWrongInput();
        }
    }

    private void prepareToNewCard() {
        showDefaultButtons();
        setHintButtonEnabled(true);
        prepareHintString();
    }

    private void prepareHintString() {
        hintArray.setLength(0);

        Card currentCard = teacher.getCurrentCard();
        for (int i = 0; i < getDefinition(currentCard).length(); ++i) {
            hintArray.append(GAP_CHAR);
        }
    }

    // Show UI actions

    private void showCurrentCard() {
        prepareToNewCard();
        bindCurrentCard();
    }

    private void showNextCard() {
        teacher.getNextCard();

        if (teacher.getCurrentCard() != null) {
            prepareToNewCard();
            bindCurrentCard();
        } else {
            onSessionFinished();
        }
    }

    private void showDefaultButtons() {
        giveUpButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        goNextButton.setVisibility(View.GONE);
    }

    private void showNextButton() {
        giveUpButton.setVisibility(View.GONE);
        checkButton.setVisibility(View.GONE);
        goNextButton.setVisibility(View.VISIBLE);
    }

    private void showHintMenu() {
        PopupMenu popupMenu = new PopupMenu(this, hintButton);
        popupMenu.inflate(R.menu.menu_hint);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.hint_show_letter) {
                    onShowNextLetterPressed();

                } else if (item.getItemId() == R.id.hint_show_random_letter) {
                    onShowRandomLetterPressed();
                }

                return false;
            }
        });

        popupMenu.show();
    }

    private void showHintString() {
        try {
            teacher.onHintShown();
        } catch (Exception e) {
            showException(e);
        }

        StringBuilder builder = new StringBuilder();
        for (int i=0; i < hintArray.length(); ++i) {
            char ch = hintArray.charAt(i);
            builder.append(ch);

            if (builder.length() < hintArray.length()*2-1) {
                builder.append(' ');
            }
        }

        inputLayout.setError(builder.toString());
    }

    // Update UI

    private void bindCurrentCard() {
        Card card = teacher.getCurrentCard();
        bindCard(card);
    }

    private void bindCard(Card card) {
        //updateCardBg(card);
        updateProgressText(card);
        termView.setText(getTerm(card));
        inputLayout.setError(null);
        inputLayout.getEditText().setText(null);
    }

    private void updateProgressText(Card card) {
        CardProgress progress = card.getProgress();
        if (progress != null) {
            String progressFormat;
            int intPorgress = (int)(progress.getProgress() * 100);
            if (progress.needHaveLesson() && intPorgress > 0) {
                progressFormat = getString(R.string.learning_is_important);
            } else {
                progressFormat = getString(R.string.learning_progress_format);
            }

            String resultString = String.format(Locale.US, progressFormat, intPorgress);
            progressTextView.setText(resultString);

        } else {
            progressTextView.setText("");
        }
    }

    private void updateCardBg(Card card) {
        float progress = card.getFloatProgress();
        int learnColor = getResources().getColor(R.color.learnProgressColor);
        int resultColor = Color.argb((int)(progress * 255), Color.red(learnColor), Color.green(learnColor), Color.blue(learnColor));
        rootView.setBackgroundColor(resultColor);
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

    private void updateHintButton() {
        setHintButtonEnabled(!isHintStringFull());
    }

    private void setHintButtonEnabled(boolean isEnable) {
        hintButton.setEnabled(isEnable);
    }

    //// Subactivities

    // Result Activity

    private void showResultActivity(LearnSession session) {
        Intent intent = new Intent(this, SessionResultActivity.class);
        intent.putExtra(SessionResultActivity.EXTERNAL_SESSION, session);
        startActivityForResult(intent, SessionResultActivity.ACTIVITY_RESULT);
    }

    private void handleResultActivityClose() {
        if (teacher.getCurrentCard() == null) {
            finish();

        } else {
            showCurrentCard();

            // to show keyboard
            HandlerTools.runOnMainThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    inputLayout.getEditText().requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputLayout.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                }
            }, 300);
        }
    }

    // Exceptions

    private void showException(Exception ex) {
        Snackbar.make(getWindow().getDecorView(), ex.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    //// Setter

    @Override
    public void setInputText(String text) {
        inputLayout.getEditText().setText(text);
    }

    //// Getters

    @Override
    public String getInputText() {
        return inputLayout.getEditText().getText().toString();
    }
}
