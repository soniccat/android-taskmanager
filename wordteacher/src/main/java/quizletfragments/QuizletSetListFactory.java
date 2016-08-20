package quizletfragments;

import android.os.Bundle;

import com.example.alexeyglushkov.quizletservice.QuizletService;
import com.example.alexeyglushkov.quizletservice.entities.QuizletSet;

import java.util.ArrayList;
import java.util.List;

import listfragment.SimpleListProvider;
import listfragment.SimpleStorableListProvider;
import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;

/**
 * Created by alexeyglushkov on 20.08.16.
 */
public class QuizletSetListFactory implements StorableListProviderFactory<QuizletSet> {
    private QuizletService service;

    public QuizletSetListFactory(QuizletService service) {
        this.service = service;
    }

    @Override
    public StorableListProvider<QuizletSet> createFromList(List<QuizletSet> list) {
        return new QuizletSetListProvider(list);
    }

    @Override
    public StorableListProvider<QuizletSet> restore(Bundle bundle) {
        StorableListProvider<QuizletSet> result = null;

        if (QuizletSetListProvider.canRestore(bundle)) {
            result = new QuizletSetListProvider();
            result.restore(bundle, service);

        } else {
            result = createDefault();
        }

        return result;
    }

    @Override
    public StorableListProvider<QuizletSet> createDefault() {
        return new StorableListProvider<QuizletSet>() {
            @Override
            public void store(Bundle bundle) {
            }

            @Override
            public void restore(Bundle bundle, Object context) {
            }

            @Override
            public List<QuizletSet> getList() {
                return service.getSets();
            }
        };
    }
}
