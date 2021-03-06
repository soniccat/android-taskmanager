package com.example.alexeyglushkov.wordteacher.courselistmodules.cardlistmodule.presenter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.listmodule.DeleteMenuListener;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 31.07.16.
 */
public class CardListPresenterMenuListener extends DeleteMenuListener<Card> {
    private CourseHolder courseHolder;

    public CardListPresenterMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        super(context, listener);
        this.courseHolder = courseHolder;
    }

    protected void fillMenu(final Card card, PopupMenu popupMenu) {
        popupMenu.getMenu().add(Menu.NONE, R.id.delete_card, 0, R.string.menu_card_delete);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.delete_card) {
                    getListener().onCardDeleteClicked(card);
                    onCardViewDeleted(card);
                }

                return false;
            }
        });
    }

    private void onCardViewDeleted(Card card) {
        deleteDataWithSnackbar(card);
    }

    @Override
    public void onRowViewDeleted(Card data) {
        onCardViewDeleted(data);
    }

    @Override
    protected void deleteData(Card data) throws Exception {
        courseHolder.removeCard(data);
    }

    @NonNull
    public Listener getListener() {
        return (Listener)this.listener;
    }

    public interface Listener extends DeleteMenuListener.Listener<Card> {
        void onCardDeleteClicked(Card data); // expect row deletion from com.example.alexeyglushkov.wordteacher.ui
    }
}
