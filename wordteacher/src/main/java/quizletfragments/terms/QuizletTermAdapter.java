package quizletfragments.terms;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;
import com.example.alexeyglushkov.wordteacher.R;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import listfragment.BaseListAdaptor;

/**
 * Created by alexeyglushkov on 03.05.16.
 */
public class QuizletTermAdapter extends BaseListAdaptor<QuizletTermAdapter.Holder, QuizletTerm> {
    List<QuizletTerm> terms = new ArrayList<>();
    Listener listener;

    public QuizletTermAdapter(Listener listener) {
        this.listener = listener;
    }

    public void updateTerms(List<QuizletTerm> newTerms) {
        terms = new ArrayList<>();
        terms.addAll(newTerms);
        notifyDataSetChanged();
    }

    public List<QuizletTerm> getTerms() {
        return terms;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_quizlet_set_card, parent, false);

        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final QuizletTerm term = terms.get(position);
        holder.nameTextview.setText(term.getTerm());
        holder.wordCountTextView.setText(term.getDefinition());

        bindListener(holder, term);
    }

    private void bindListener(Holder holder, final QuizletTerm term) {
        holder.termView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTermClicked(v, term);
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
        return terms.size();
    }

    @Override
    public int getDataIndex(QuizletTerm data) {
        return terms.indexOf(data);
    }

    @Override
    public void deleteDataAtIndex(int index) {
        Assert.fail("Not supported");
    }

    public class Holder extends RecyclerView.ViewHolder {
        public View termView;
        public TextView nameTextview;
        public TextView wordCountTextView;
        public ImageView menuButton;

        public Holder(View itemView) {
            super(itemView);
            termView = itemView.findViewById(R.id.card);
            nameTextview = (TextView)itemView.findViewById(R.id.name);
            wordCountTextView = (TextView)itemView.findViewById(R.id.wordCount);
            menuButton = (ImageView)itemView.findViewById(R.id.menuButton);
        }
    }

    public interface Listener {
        void onTermClicked(View view, QuizletTerm card);
        void onMenuClicked(View view, QuizletTerm card);
    }
}
