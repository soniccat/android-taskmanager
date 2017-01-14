package learning;

import android.content.Intent;

import model.Card;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public interface LearnView {
    Intent getIntent();
    void setViewResult(int result);

    void setInputText(String text);
    String getInputText();

    void bindCard(Card card, String term);
}
