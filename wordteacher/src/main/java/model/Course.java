package model;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class Course {
    private UUID id;
    private List<Card> cards = new ArrayList<>();

    public Course() {
        id = UUID.randomUUID();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void removeCard(Card card) {
        cards.remove(card);
    }

    public UUID getId() {
        return id;
    }
}
