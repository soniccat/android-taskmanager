package quizletfragments;

import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.List;

/**
 * Created by alexeyglushkov on 14.08.16.
 */
public class QuizletSimpleSetListProvider implements QuizletSetListProvider {
    private List<QuizletSet> sets;

    public QuizletSimpleSetListProvider(List<QuizletSet> sets) {
        this.sets = sets;
    }

    @Override
    public List<QuizletSet> getSets() {
        return sets;
    }
}
