package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.view;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.tools.TimeTools;
import com.example.alexeyglushkov.wordteacher.R;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.example.alexeyglushkov.wordteacher.listmodule.view.BaseListAdaptor;
import com.example.alexeyglushkov.wordteacher.model.Card;
import com.example.alexeyglushkov.wordteacher.model.CardProgress;
import com.example.alexeyglushkov.wordteacher.tools.DeleteTouchHelper;
import com.example.alexeyglushkov.wordteacher.model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseListAdapter extends BaseListAdaptor<CourseListAdapter.ViewHolder, Course> implements DeleteTouchHelper.Listener {
    private Listener listener;
    private ItemTouchHelper deleteTouchHelper;

    //// Initialization

    public CourseListAdapter(Listener listener) {
        this.listener = listener;
        this.deleteTouchHelper = new ItemTouchHelper(new DeleteTouchHelper(this));
    }

    //// Events

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        deleteTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Course course = getItems().get(position);
        bindWordNameTextView(holder, course);
        bindWordCountTextView(holder, course);
        bindProgressTextView(holder, course);
        bindListener(holder, course);
    }

    //// Actions

    @Override
    public void cleanup() {
        super.cleanup();
        listener = null;
        deleteTouchHelper = null;
    }

    private void bindWordNameTextView(ViewHolder holder, Course course) {
        holder.nameTextview.setText(course.getTitle());
    }

    private void bindWordCountTextView(ViewHolder holder, Course course) {
        String format = holder.itemView.getContext().getResources().getString(R.string.set_word_count_formant);
        String description = String.format(Locale.US, format, course.getCards().size());
        holder.wordCountTextView.setText(description);
    }

    private void bindProgressTextView(ViewHolder holder, Course course) {
        int inProgressCount = course.getInProgressCards().size();
        if (inProgressCount > 0 && inProgressCount == course.getCards().size()) {
            holder.inProgressTextView.setVisibility(View.VISIBLE);

            List<Card> waitingCards = course.getReadyToLearnCards();
            int waitingCardCount = waitingCards.size();
            if (waitingCardCount != 0) {
                String waitingFormat = holder.itemView.getContext().getResources().getString(R.string.cell_course_waiting_words);
                String inProgressString = String.format(Locale.US, waitingFormat, waitingCardCount);
                holder.inProgressTextView.setText(inProgressString);

            } else {
                Date date = getEarliestWaitCard(course.getCards());
                if (date != null) {
                    long duration = date.getTime() - new Date().getTime();
                    String format = holder.itemView.getContext().getString(R.string.cell_course_revise_time);
                    String timeString = String.format(Locale.US, format, TimeTools.getDurationString(duration));
                    holder.inProgressTextView.setText(timeString);
                } else {
                    holder.inProgressTextView.setText(R.string.cell_course_no_waiting_words);
                }
            }

        } else if (inProgressCount > 0) {
            holder.inProgressTextView.setVisibility(View.VISIBLE);
            String inProgressFormat = holder.itemView.getContext().getResources().getString(R.string.cell_course_in_progress);
            String inProgressString = String.format(Locale.US, inProgressFormat, inProgressCount);
            holder.inProgressTextView.setText(inProgressString);

        } else {
            holder.inProgressTextView.setText(null);
            holder.inProgressTextView.setVisibility(View.INVISIBLE);
        }
    }

    private Date getEarliestWaitCard(List<Card> waitingCards) {
        Date result = null;
        for (Card card : waitingCards) {
            CardProgress progress = card.getProgress();
            if (progress != null && !progress.isCompleted()) {
                Date lessonDate = progress.getNextLessonDate();
                if (result != null) {
                    result = lessonDate.compareTo(result) == -1 ? lessonDate : result;
                } else {
                    result = lessonDate;
                }
            }
        }

        return result;
    }

    private void bindListener(ViewHolder holder, final Course course) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCourseClicked(v, course);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCourseMenuClicked(v, course);
            }
        });
    }

    public void onItemDeleted(RecyclerView.ViewHolder holder, int index, int position) {
        Course course = getItems().get(index);
        listener.onCourseViewDeleted(holder.itemView, course);

        deleteDataAtIndex(index);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View cardView;
        public TextView nameTextview;
        public TextView wordCountTextView;
        public TextView inProgressTextView;
        public ImageView menuButton;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card);
            nameTextview = (TextView)itemView.findViewById(R.id.name);
            wordCountTextView = (TextView)itemView.findViewById(R.id.wordCount);
            menuButton = (ImageView)itemView.findViewById(R.id.menuButton);
            inProgressTextView = (TextView)itemView.findViewById(R.id.inProgressCount);
        }
    }

    public interface Listener {
        void onCourseClicked(View view, Course card);
        void onCourseMenuClicked(View view, Course card);
        void onCourseViewDeleted(View view, Course course);
    }
}
