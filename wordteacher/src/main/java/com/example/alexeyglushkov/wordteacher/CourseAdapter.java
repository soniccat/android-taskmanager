package com.example.alexeyglushkov.wordteacher;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Course;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private ArrayList<Course> courses = new ArrayList<>();
    private Listener listener;

    public CourseAdapter(Listener listener) {
        this.listener = listener;
    }

    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("courses", courses);
        return bundle;
    }

    public void onRestoreInstanceState (Parcelable state) {
        Bundle bundle = (Bundle)state;
        courses = bundle.getParcelableArrayList("courses");
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Course course = courses.get(position);
        bindWordNameTextView(holder, course);
        bindWordCountTextView(holder, course);
        bindProgressTextView(holder, course);
        bindListener(holder, course);
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

            int waitingCardCount = course.getReadyToLearnCards().size();
            if (waitingCardCount != 0) {
                String waitingFormat = holder.itemView.getContext().getResources().getString(R.string.cell_course_waiting_words);
                String inProgressString = String.format(Locale.US, waitingFormat, waitingCardCount);
                holder.inProgressTextView.setText(inProgressString);

            } else {
                holder.inProgressTextView.setText(R.string.cell_course_no_waiting_words);
            }

        } else if (inProgressCount > 0) {
            holder.inProgressTextView.setVisibility(View.VISIBLE);
            String inPorgressFormat = holder.itemView.getContext().getResources().getString(R.string.cell_course_in_progress);
            String inProgressString = String.format(Locale.US, inPorgressFormat, inProgressCount);
            holder.inProgressTextView.setText(inProgressString);

        } else {
            holder.inProgressTextView.setText(null);
            holder.inProgressTextView.setVisibility(View.INVISIBLE);
        }
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

    @Override
    public int getItemCount() {
        return courses.size();
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
    }
}
