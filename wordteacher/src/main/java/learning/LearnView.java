package learning;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import model.Card;

/**
 * Created by alexeyglushkov on 08.01.17.
 */

public interface LearnView {
    Intent getIntent();
    Context getContext();
    void setViewResult(int result);
    void finish();
    void startActivityForResult(Intent intent, int code);

    void setInputText(String text);
    String getInputText();
    void showInputFocus();

    void bindCard(Card card, String term);
    void showHintString(String hint, boolean isHintFull);
    void showException(Exception ex);
    void showInputError(String error);
    void showNextButton();
}
