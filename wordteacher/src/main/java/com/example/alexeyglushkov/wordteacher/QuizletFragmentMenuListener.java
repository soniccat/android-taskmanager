package com.example.alexeyglushkov.wordteacher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.List;

import model.Card;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class QuizletFragmentMenuListener implements QuizletCardsFragment.Listener {
    private Context context;
    private CourseHolder courseHolder;
    private Listener listener;

    public QuizletFragmentMenuListener(Context context, CourseHolder courseHolder, Listener listener) {
        this.context = context;
        this.courseHolder = courseHolder;
        this.listener = listener;
    }

    private Context getContext() {
        return context;
    }

    private CourseHolder getCourseHolder() {
        return courseHolder;
    }

    private void showAddFromSetDialog(final List<QuizletTerm> terms) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        List<String> rows = new ArrayList<>();
        final ArrayList<Course> courses = getCourseHolder().getCourses();
        for (Course course : courses) {
            rows.add(course.getTitle());
        }

        ListAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.row_text_view, rows);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Course course = courses.get(which);
                addCardsToCourse(course, terms);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        String title = context.getString(R.string.dialog_choose_course);
        builder.setTitle(title);
        builder.show();
    }

    private void createCourseFromCard(QuizletTerm quizletTerm, String name) {
        Card card = createCard(quizletTerm);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(card);

        createCourse(name, cards);
    }

    private void createCourse(String title, ArrayList<Card> cards) {
        Course course = new Course();
        course.setTitle(title);
        course.addCards(cards);

        Error error = getCourseHolder().addCourse(course);
        if (error != null) {
            listener.onCourseCreated(course);
        }
    }

    private void addCardsToCourse(Course course, List<QuizletTerm> terms) {
        List<Card> cards = new ArrayList<>();
        for (QuizletTerm term : terms) {
            Card card = createCard(term);
            cards.add(card);
        }

        if (getCourseHolder().addNewCards(course, cards)) {
            listener.onCardsAdded(course);
        }
    }

    private void onCreateCourseFromCard(final QuizletTerm card) {
        final RenameAlert renameAlert = new RenameAlert();
        renameAlert.setPositiveButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseFromCard(card, renameAlert.getName());
            }
        });
        renameAlert.show(getContext(), listener.getDialogContainer());
    }

    private void onCreateCourseFromSet(QuizletSet set) {
        ArrayList<Card> cards = new ArrayList<>();
        for (QuizletTerm term : set.getTerms()) {
            Card card = createCard(term);
            cards.add(card);
        }

        createCourse(set.getTitle(), cards);
    }

    @NonNull
    private Card createCard(QuizletTerm term) {
        Card card = new Card();
        card.setTerm(term.getTerm());
        card.setDefinition(term.getDefinition());
        card.setQuizletTerm(term);
        return card;
    }

    // QuizletCardsFragment.Listener

    @Override
    public void onSetClicked(QuizletSet set) {
        listener.onSetClicked(set);
    }

    @Override
    public void onSetMenuClicked(final QuizletSet set, View v) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), v);
        popupMenu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        if (getCourseHolder().getCourses().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.add_to_course, 0, R.string.menu_add_to_course);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromSet(set);

                } else if (item.getItemId() == R.id.add_to_course) {
                    showAddFromSetDialog(set.getTerms());
                }

                return false;
            }
        });

        popupMenu.show();
    }

    @Override
    public void onTermClicked(QuizletTerm card) {

    }

    @Override
    public void onTermMenuClicked(final QuizletTerm term, View v) {
        PopupMenu popupMenu = new PopupMenu(this.getContext(), v);
        popupMenu.getMenu().add(Menu.NONE, R.id.create_set, 0, R.string.menu_create_course);
        if (getCourseHolder().getCourses().size() > 0) {
            popupMenu.getMenu().add(Menu.NONE, R.id.add_to_course, 0, R.string.menu_add_to_course);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.create_set) {
                    onCreateCourseFromCard(term);

                } else if (item.getItemId() == R.id.add_to_course) {
                    ArrayList<QuizletTerm> terms = new ArrayList<QuizletTerm>();
                    terms.add(term);
                    showAddFromSetDialog(terms);
                }

                return false;
            }
        });

        popupMenu.show();
    }

    public interface Listener {
        void onSetClicked(QuizletSet set);
        void onCourseCreated(Course course);
        void onCardsAdded(Course course);
        ViewGroup getDialogContainer();
    }
}
