package quizletfragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.List;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public interface QuizletSetListProvider {
    List<QuizletSet> getSets();
}
