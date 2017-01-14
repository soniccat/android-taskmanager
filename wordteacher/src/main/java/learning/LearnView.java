package learning;

import android.content.Intent;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public interface LearnView {
    Intent getIntent();

    void setInputText(String text);
    String getInputText();
}
