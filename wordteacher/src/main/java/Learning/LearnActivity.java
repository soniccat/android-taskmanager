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

    public final static String EXTRA_COURSE = "Course";

    private CourseTeacher teacher;

    private TextView titleView;
    private TextInputLayout inputLayout;
    private Button giveUpButton;
    private Button checkButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_learn);
        titleView = (TextView)findViewById(R.id.word);
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
        teacher = new CourseTeacher(course);
        bindCurrentCard();
    }

    private void bindCurrentCard() {
        Card card = teacher.getCurrentCard();

        bindCard(card);
    }

    private void bindCard(Card card) {
        titleView.setText(card.getTerm());
        inputLayout.setError(null);
    }

    private void checkInput() {
        Card card = teacher.getCurrentCard();
        String input = inputLayout.getEditText().getText().toString();
        if (input.equalsIgnoreCase(card.getDefinition())) {
            onRightInput();
        } else {
            onWrongInput();
        }
    }

    private void onRightInput() {
        Card card = teacher.getNextCard();
        bindCard(card);
    }

    private void onWrongInput() {
        inputLayout.setError(getString(R.string.error_wrong_input));
    }

    private void giveUp() {
        Card card = teacher.getCurrentCard();
        inputLayout.getEditText().setText("");

        String format = getString(R.string.error_answer_format);
        String answerString = String.format(Locale.US, format, card.getDefinition());
        inputLayout.setError(answerString);
    }
}
