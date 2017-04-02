package com.example.alexeyglushkov.wordteacher.sessionresultmodule.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexeyglushkov.wordteacher.R;

import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListAdaptor;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.SessionCardResult;

/**
 * Created by alexeyglushkov on 02.06.16.
 */
public class SessionResultAdapter extends BaseListAdaptor<SessionResultAdapter.ViewHolder, SessionResultAdapterView> {
    public SessionResultAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_session_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SessionResultAdapterView item = items.get(position);
        Card card = item.card;

        holder.termTextView.setText(card.getTerm());
        holder.definitionTextView.setText(card.getDefinition());

        int color = getBgColor(holder.itemView.getContext(), item.result);
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
