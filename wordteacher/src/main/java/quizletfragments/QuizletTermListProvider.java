package quizletfragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public interface QuizletTermListProvider {
    List<QuizletTerm> getQuizletTerms();
}
