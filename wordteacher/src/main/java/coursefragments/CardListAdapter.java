package coursefragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.List;

import listfragment.BaseListAdaptor;
import tools.DeleteTouchHelper;
import model.Card;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class CardListAdapter extends BaseListAdaptor<CardListAdapter.Holder, Card> implements DeleteTouchHelper.Listener {
    private ArrayList<Card> cards = new ArrayList<>();
    private Listener listener;
    private ItemTouchHelper deleteTouchHelper;

    public ArrayList<Card> getCards() {
        return cards;
    }

    public CardListAdapter(Listener listener) {
        this.listener = listener;
        this.deleteTouchHelper = new ItemTouchHelper(new DeleteTouchHelper(this));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        deleteTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void updateCards(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
        notifyDataSetChanged();
    }

    @Override
    public int getDataIndex(Card data) {
        return getCardIndex(data);
    }

    @Override
    public void deleteDataAtIndex(int index) {
        deleteCardAtIndex(index);
    }

    public int getCardIndex(Card card) {
        return cards.indexOf(card);
    }

    public void deleteCardAtIndex(int index) {
        cards.remove(index);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final Card term = cards.get(position);
        holder.nameTextview.setText(term.getTerm());
        holder.wordCountTextView.setText(term.getDefinition());

        bindListener(holder, term);
    }

    private void bindListener(Holder holder, final Card term) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCardClicked(v, term);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMenuClicked(v, term);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void onItemDeleted(RecyclerView.ViewHolder holder, int index, int position) {
        Card course = cards.get(index);
        listener.onCardViewDeleted(holder.itemView, course);
        deleteCardAtIndex(index);
        notifyItemRemoved(position);
    }

    public class Holder extends RecyclerView.ViewHolder {
        public View cardView;
        public TextView nameTextview;
        public TextView wordCountTextView;
        public ImageView menuButton;

        public Holder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            nameTextview = (TextView)itemView.findViewById(R.id.name);
            wordCountTextView = (TextView)itemView.findViewById(R.id.wordCount);
            menuButton = (ImageView)itemView.findViewById(R.id.menuButton);
        }
    }

    public interface Listener {
        void onCardClicked(View view, Card card);
        void onMenuClicked(View view, Card card);
        void onCardViewDeleted(View view, Card course);
    }
}
