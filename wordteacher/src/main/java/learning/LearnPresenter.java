package learning;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public interface LearnPresenter extends LearnModule {
    void onCreate(@Nullable Bundle savedInstanceState);
    void onSaveInstanceState(Bundle outState);
    void onDestroy();

    void onCheckPressed();
    void onNextPressed();
    void onTextChanged();
    void onShowNextLetterPressed();
}
