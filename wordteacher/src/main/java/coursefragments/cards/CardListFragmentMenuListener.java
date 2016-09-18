package coursefragments.cards;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexeyglushkov.wordteacher.R;

import listfragment.DeleteMenuListener;
import listfragment.ListMenuListener;
import model.Card;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 31.07.16.
 */
public class CardListFragmentMenuListener extends DeleteMenuListener<Card> {
    private CourseHolder courseHolder;

    public CardListFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
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

    public void onCardViewDeleted(Card card) {
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

    public Listener getListener() {
        return (Listener)this.listener;
    }

    public interface Listener extends DeleteMenuListener.Listener<Card> {
        void onCardDeleteClicked(Card data); // expect row deletion from ui
    }
}
