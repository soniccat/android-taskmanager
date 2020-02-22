package com.example.alexeyglushkov.wordteacher.learningmodule.presenter;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.example.alexeyglushkov.wordteacher.learningmodule.LearnModule;
import com.example.alexeyglushkov.wordteacher.learningmodule.view.LearnView;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public interface LearnPresenter extends LearnModule {
    void onCreate(@Nullable Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onDestroy();
    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onCheckPressed();
    void onNextPressed();
    void onTextChanged();
    void onShowNextLetterPressed();
    void onShowRandomLetterPressed();
    void onGiveUpPressed();

    void setView(LearnView view);
}
