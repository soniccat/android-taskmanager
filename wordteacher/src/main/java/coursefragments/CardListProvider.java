package coursefragments;

import java.util.List;

import model.Card;

/**
 * Created by alexeyglushkov on 06.08.16.
 */
public interface CardListProvider {
    List<Card> getCards();
}
