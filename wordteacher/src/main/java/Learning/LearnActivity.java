package learning;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.Locale;

import main.BaseActivity;
import model.Card;
import model.Course;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
public class LearnActivity extends BaseActivity {

    public final static String EXTRA_DEFINITION_TO_TERM = "EXTRA_DEFINITION_TO_TERM";
    public final static String EXTRA_COURSE = "EXTRA_COURSE";

    private CourseTeacher teacher;

    private TextView termView;
    private TextInputLayout inputLayout;
    private Button giveUpButton;
    private Button checkButton;

    private boolean definitionToTerm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_learn);
        termView = (TextView)findViewById(R.id.word);
        inputLayout = (TextInputLayout)findViewById(R.id.definition);
        giveUpButton = (Button)findViewById(R.id.giveUpButton);
        checkButton = (Button)findViewById(R.id.checkButton);

        giveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUp();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInput();
            }
        });

        Course course = getIntent().getParcelableExtra(EXTRA_COURSE);
        definitionToTerm = getIntent().getBooleanExtra(EXTRA_DEFINITION_TO_TERM, false);

        teacher = new CourseTeacher(course);
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

    private void checkInput() {
        Card card = teacher.getCurrentCard();
        String input = inputLayout.getEditText().getText().toString();
        if (input.equalsIgnoreCase(getDefinition(card))) {
            onRightInput();
        } else {
            onWrongInput();
        }
    }

    private void onRightInput() {
        Card card = teacher.getNextCard();
        if (card != null) {
            bindCard(card);
        } else {
            onFinished();
        }
    }

    private void onWrongInput() {
        inputLayout.setError(getString(R.string.error_wrong_input));
    }

    private void onFinished() {

    }

    private void giveUp() {
        Card card = teacher.getCurrentCard();
        inputLayout.getEditText().setText("");

        String format = getString(R.string.error_answer_format);
        String answerString = String.format(Locale.US, format, getDefinition(card));
        inputLayout.setError(answerString);
    }
}
