package quizletfragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;
import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSetTermListProvider implements QuizletTermListProvider {
    private QuizletSet set;

    public QuizletSetTermListProvider(QuizletSet set) {
        this.set = set;
    }

    public QuizletSet getSet() {
        return set;
    }

    @Override
    public List<QuizletTerm> getQuizletTerms() {
        return this.getQuizletTerms();
    }
}
