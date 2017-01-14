package com.example.alexeyglushkov.wordteacher.learningmodule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.alexeyglushkov.wordteacher.model.LearnSession;
import com.example.alexeyglushkov.wordteacher.model.Card;

/**
 * Created by alexeyglushkov on 02.06.16.
 */
public class SessionResultAdapter extends RecyclerView.Adapter<SessionResultAdapter.ViewHolder> {
    private LearnSession session;
    private List<SessionCardResult> results;

    public void setSession(LearnSession aSession) {
        this.session = aSession;

        List<SessionCardResult> resultCopy = new ArrayList<>(aSession.getResults());
        results = sortResults(resultCopy);
        notifyDataSetChanged();
    }

    private List<SessionCardResult> sortResults(List<SessionCardResult> results) {
        Collections.sort(results, new Comparator<SessionCardResult>() {
            @Override
            public int compare(SessionCardResult lhs, SessionCardResult rhs) {
                return SessionResultAdapter.compare(lhs.isRight, rhs.isRight);
            }
        });
        return results;
    }

    public static int compare(boolean lhs, boolean rhs) {
        return lhs == rhs ? 0 : lhs ? 1 : -1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_session_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SessionCardResult result = results.get(position);
        int cardPosition = session.getResults().indexOf(result);

        Card card = session.getCard(cardPosition);

        holder.termTextView.setText(card.getTerm());
        holder.definitionTextView.setText(card.getDefinition());

        int color = getBgColor(holder.itemView.getContext(), result);
        holder.itemView.setBackgroundColor(color);
    }

    private int getBgColor(Context context, SessionCardResult result) {
        float progressChange = result.newProgress - result.oldProgress;
        int color;
        if (progressChange > 0 || result.isRight) {
            color = context.getResources().getColor(R.color.positiveProgress);
        } else {
            color = context.getResources().getColor(R.color.negativeProgress);
        }
        return color;
    }

    @Override
    public int getItemCount() {
        return session.getSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView termTextView;
        TextView definitionTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            termTextView = (TextView)itemView.findViewById(R.id.termTextView);
            definitionTextView = (TextView)itemView.findViewById(R.id.definitionTextView);
        }
    }
}
