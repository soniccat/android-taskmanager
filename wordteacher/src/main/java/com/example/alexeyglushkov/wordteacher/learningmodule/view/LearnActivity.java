package com.example.alexeyglushkov.wordteacher.learningmodule.view;

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

import java.util.Locale;

import com.example.alexeyglushkov.wordteacher.learningmodule.presenter.LearnPresenter;
import com.example.alexeyglushkov.wordteacher.main.BaseActivity;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.CardProgress;

/**
 * Created by alexeyglushkov on 09.05.16.
 */
// TODO: consider moving content to fragment
public class LearnActivity extends BaseActivity implements LearnView{

    @NonNull
    LearnPresenter presenter;

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

        presenter = createPresenter();
        presenter.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            restore(savedInstanceState);
        }
    }

    private void restore(Bundle savedInstanceState) {
        // because of the support lib bug
        String string = savedInstanceState.getString("input");
        setInputText(string);
        inputLayout.getEditText().setSelection(string.length());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // because of the support lib bug
        outState.putString("input", getInputText());

        presenter.onSaveInstanceState(outState);
    }

    // Binding

    private void bindViews() {
        rootView = findViewById(R.id.root);
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
                presenter.onCheckPressed();
            }
        });
        goNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNextPressed();
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
                    presenter.onCheckPressed();
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
        presenter.onDestroy();
    }

    private void onTextChanged() {
        presenter.onTextChanged();
    }

    private void onShowNextLetterPressed() {
        presenter.onShowNextLetterPressed();
    }

    private void onShowRandomLetterPressed() {
        presenter.onShowRandomLetterPressed();
    }

    private void onGiveUpPressed() {
        presenter.onGiveUpPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    //// Actions

    private void prepareToNewCard() {
        showDefaultButtons();
        setHintButtonEnabled(true);
    }

    @Override
    public void showInputFocus() {
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

    // Show UI actions

    private void showDefaultButtons() {
        giveUpButton.setVisibility(View.VISIBLE);
        checkButton.setVisibility(View.VISIBLE);
        goNextButton.setVisibility(View.GONE);
    }

    public void showNextButton() {
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

    public void showHintString(String hintString, boolean isHintFull) {
        inputLayout.setError(hintString);
        setHintButtonEnabled(!isHintFull);
    }

    public void showInputError(String error) {
        inputLayout.setError(error);
    }

    // Update UI

    public void bindCard(Card card, String term) {
        prepareToNewCard();

        //updateCardBg(card);
        updateProgressText(card);
        termView.setText(term);
        inputLayout.setError(null);
        setInputText(null);
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

    private void setHintButtonEnabled(boolean isEnable) {
        hintButton.setEnabled(isEnable);
    }

    //// Subactivities

    // Exceptions

    public void showException(Exception ex) {
        Snackbar.make(getWindow().getDecorView(), ex.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    //// Creation Methods

    private LearnPresenter createPresenter() {
        LearnPresenter presenter = null;
        String presenterName = this.getResources().getString(R.string.learning_presenter_class);
        try {
            presenter = (LearnPresenter) getClassLoader().loadClass(presenterName).newInstance();
            presenter.setView(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return presenter;
    }

    //// Setter

    @Override
    public void setInputText(String text) {
        inputLayout.getEditText().setText(text);
    }

    public void setViewResult(int result) {
        setResult(result, getIntent());
    }

    //// Getters

    @Override
    public String getInputText() {
        return inputLayout.getEditText().getText().toString();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
