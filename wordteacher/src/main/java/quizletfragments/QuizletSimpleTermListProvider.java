package quizletfragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.List;

/**
 * Created by alexeyglushkov on 07.08.16.
 */
public class QuizletSimpleTermListProvider implements QuizletTermListProvider {
    private List<QuizletTerm> terms;

    public QuizletSimpleTermListProvider(List<QuizletTerm> terms) {
        this.terms = terms;
    }

    @Override
    public List<QuizletTerm> getQuizletTerms() {
        return terms;
    }
}
