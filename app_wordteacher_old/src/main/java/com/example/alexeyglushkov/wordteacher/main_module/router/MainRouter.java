package com.example.alexeyglushkov.wordteacher.main_module.router;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.alexeyglushkov.wordteacher.model.Card;

import java.util.List;

/**
 * Created by alexeyglushkov on 12.02.17.
 */

public interface MainRouter {
    void showLearningModule(@NonNull Context context, @NonNull List<Card> cards);
}
