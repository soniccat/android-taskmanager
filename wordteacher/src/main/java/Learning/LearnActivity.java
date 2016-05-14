package learning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import main.BaseActivity;
import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class LearnActivity extends BaseActivity {

    public final static String EXTRA_DEFINITION_TO_TERM = "EXTRA_DEFINITION_TO_TERM";
    public final static String EXTRA_COURSE = "EXTRA_COURSE";
    public final static Character GAP_CHAR = '_';

    private CourseTeacher teacher;

    private TextView termView;
    private TextInputLayout inputLayout;
    private Button giveUpButton;
    private Button checkButton;
    private Button goNextButton;
    private ImageButton hintButton;

    private boolean definitionToTerm;
    private boolean needShowNextButtonIfCorrect;
    private boolean isNextLetterHintUsed;
    private boolean isRandomLetterHintUsed;
    private int numberOfChecks;
    private ArrayList<Character> hintArray = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_learn);
        termView = (TextView)findViewById(R.id.word);
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
                onRightInput();
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

        Course course = getIntent().getParcelableExtra(EXTRA_COURSE);
        definitionToTerm = getIntent().getBooleanExtra(EXTRA_DEFINITION_TO_TERM, false);

        teacher = new CourseTeacher(course);
        prepareToNewCard();
        bindCurrentCard();
    }

    private void bindCurrentCard() {
        Card card = teacher.getCurrentCard();

        bindCard(card);
    }

    private void bindCard(Card card) {
        termView.setText(getTerm(card));
        inputLayout.setError(null);
        inputLayout.getEditText().setText(null);
    }

    private String getTerm(Card card) {
        return definitionToTerm ? card.getDefinition() : card.getTerm();
    }

    private String getDefinition(Card card) {
        return definitionToTerm ? card.getTerm() : card.getDefinition();
    }

    private void onTextChanged() {
        if (isInputCorrect() && needShowNextButtonIfCorrect) {
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
        ++numberOfChecks;

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
        Card card = teacher.getNextCard();
        prepareToNewCard();

        if (card != null) {
            bindCard(card);
        } else {
            onFinished();
        }
    }

    private void prepareToNewCard() {
        showDefaultButtons();
        needShowNextButtonIfCorrect = false;
        numberOfChecks = 0;
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
        needShowNextButtonIfCorrect = true;
        inputLayout.setError(getString(R.string.error_wrong_input));
    }

    private void onFinished() {

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
        needShowNextButtonIfCorrect = true;

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
        needShowNextButtonIfCorrect = true;
        inputLayout.getEditText().setText("");

        String definition = getDefinition(teacher.getCurrentCard());
        for (int i=0; i<definition.length(); ++i) {
            hintArray.set(i, definition.charAt(i));
        }

        showHintString();
        updateHintButton();
    }
}
