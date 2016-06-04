package learning;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alexeyglushkov.taskmanager.task.Tools;
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
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
// TODO: consider moving content to fragment
public class LearnActivity extends BaseActivity {

    public static final int ACTIVITY_RESULT = 10002;
    public static final int ACTIVITY_RESULT_CODE = 1;
    public final static String EXTRA_DEFINITION_TO_TERM = "EXTRA_DEFINITION_TO_TERM";
    public final static String EXTRA_CARD_IDS = "EXTRA_CARD_IDS";
    public final static Character GAP_CHAR = '_';

    private CardTeacher teacher;

    private View rootView;
    private TextView termView;
    private TextView progressTextView;
    private TextInputLayout inputLayout;
    private Button giveUpButton;
    private Button checkButton;
    private Button goNextButton;
    private ImageButton hintButton;

    private boolean definitionToTerm;
    private ArrayList<Character> hintArray = new ArrayList<>();

    private MainApplication getMainApplication() {
        return MainApplication.instance;
    }

    public CourseHolder getCourseHolder() {
        return getMainApplication().getCourseHolder();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_learn);
        rootView = (View)findViewById(R.id.root);
        termView = (TextView)findViewById(R.id.word);
        progressTextView = (TextView)findViewById(R.id.progressTextView);
        inputLayout = (TextInputLayout)findViewById(R.id.definition);
        giveUpButton = (Button)findViewById(R.id.giveUpButton);
        checkButton = (Button)findViewById(R.id.checkButton);
        goNextButton = (Button)findViewById(R.id.go_next_button);
        hintButton = (ImageButton)findViewById(R.id.hit_button);

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

        setResult(ACTIVITY_RESULT_CODE, getIntent());
        createTeacher();

        definitionToTerm = getIntent().getBooleanExtra(EXTRA_DEFINITION_TO_TERM, false);

        showCurrentCard();
    }

    private void createTeacher() {
        String[] courseIdStrings = getIntent().getStringArrayExtra(EXTRA_CARD_IDS);
        List<Card> cards = new ArrayList<>();
        for (String id : courseIdStrings) {
            UUID cardId = UUID.fromString(id);
            Card card = getCourseHolder().getCard(cardId);
            cards.add(card);
        }

        teacher = new CardTeacher(cards);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("teacher", teacher);
        outState.putBoolean("definitionToTerm", definitionToTerm);

    }

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

    private String getTerm(Card card) {
        return definitionToTerm ? card.getDefinition() : card.getTerm();
    }

    private String getDefinition(Card card) {
        return definitionToTerm ? card.getTerm() : card.getDefinition();
    }

    private void onTextChanged() {
        if (isInputCorrect() && teacher.isWrongAnswerCounted()) {
            showNextButton();
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

    private void checkInput() {
        teacher.onCheckInput();

        if (isInputCorrect()) {
            onRightInput();
        } else {
            onWrongInput();
        }
    }

    private boolean isInputCorrect() {
        Card card = teacher.getCurrentCard();
        String input = inputLayout.getEditText().getText().toString();
        return input.equalsIgnoreCase(getDefinition(card));
    }

    private void onRightInput() {
        teacher.onRightInput();
        showNextCard();
    }

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

    private void prepareToNewCard() {
        showDefaultButtons();
        setHintButtonEnabled(true);
        prepareHintString();
    }

    private void prepareHintString() {
        hintArray.clear();

        Card currentCard = teacher.getCurrentCard();
        for (int i=0; i<getDefinition(currentCard).length(); ++i) {
            hintArray.add(GAP_CHAR);
        }
    }

    private void onWrongInput() {
        teacher.onWrongInput();
        inputLayout.setError(getString(R.string.error_wrong_input));
    }

    private void onSessionFinished() {
        LearnSession session = teacher.getCurrentSession();
        teacher.onSessionsFinished();

        Intent intent = new Intent(this, SessionResultActivity.class);
        intent.putExtra(SessionResultActivity.EXTERNAL_SESSION, session);
        startActivityForResult(intent, SessionResultActivity.ACTIVITY_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SessionResultActivity.ACTIVITY_RESULT) {
            if (teacher.getCurrentCard() == null) {
                finish();
            } else {
                showCurrentCard();

                // to show keyboard
                Tools.runOnMainThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inputLayout.getEditText().requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(inputLayout.getEditText(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }, 300);
            }
        }
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

    private void onShowNextLetterPressed() {
        int index = hintArray.indexOf(GAP_CHAR);
        updateHintString(index);
        showHintString();
        updateHintButton();
    }

    private void updateHintButton() {
        setHintButtonEnabled(!isHintStringFull());
    }

    private void setHintButtonEnabled(boolean isEnable) {
        hintButton.setEnabled(isEnable);
    }

    private void onShowRandomLetterPressed() {
        updateHintStringWithRandomLetter();
        showHintString();
        updateHintButton();
    }

    private boolean isHintStringFull() {
        return getHintGapCount() == 0;
    }

    private void updateHintStringWithRandomLetter() {
        int gapCount = getHintGapCount();
        int random = Math.abs(new Random(new Date().getTime()).nextInt());
        int gapIndex = random % gapCount;
        updateHintString(gapIndexToCharIndex(gapIndex));
    }

    private int getHintGapCount() {
        int gapCount = 0;
        for (int i=0; i<hintArray.size(); ++i) {
            if (hintArray.get(i) == GAP_CHAR) {
                ++gapCount;
            }
        }
        return gapCount;
    }

    private int gapIndexToCharIndex(int searchGapIndex) {
        int gapIndex = -1;
        int position = -1;
        for (int i=0; i<hintArray.size(); ++i) {
            if (hintArray.get(i) == GAP_CHAR) {
                ++gapIndex;
                if (gapIndex == searchGapIndex) {
                    position = i;
                    break;
                }
            }
        }

        return position;
    }

    private void updateHintString(int index) {
        String definition = getDefinition(teacher.getCurrentCard());
        hintArray.set(index, definition.charAt(index));
    }

    private void showHintString() {
        teacher.onHintShown();

        StringBuilder builder = new StringBuilder();
        for (Character ch : hintArray) {
            builder.append(ch);

            if (builder.length() < hintArray.size()*2-1) {
                builder.append(' ');
            }
        }

        inputLayout.setError(builder.toString());
    }

    private void onGiveUpPressed() {
        teacher.onGiveUp();
        inputLayout.getEditText().setText("");

        String definition = getDefinition(teacher.getCurrentCard());
        for (int i=0; i<definition.length(); ++i) {
            hintArray.set(i, definition.charAt(i));
        }

        showHintString();
        updateHintButton();
    }
}
