package com.example.alexeyglushkov.wordteacher.learningmodule.router;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;

/**
 * Created by alexeyglushkov on 02.04.17.
 */

public interface LearnRouter {
    void showResultModule(@NonNull Context context, LearnSession session);
}
