package model;

import com.example.alexeyglushkov.quizletservice.entities.QuizletTerm;

import java.util.Date;
import java.util.UUID;

/**
 * Created by alexeyglushkov on 07.05.16.
 */
public class Card {
    private UUID id;
    private Date createDate;
    private String term;
    private String definition;

    private QuizletTerm quizletTerm;

    public Card() {
        id = UUID.randomUUID();
        createDate = new Date();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public QuizletTerm getQuizletTerm() {
        return quizletTerm;
    }

    public void setQuizletTerm(QuizletTerm quizletTerm) {
        this.quizletTerm = quizletTerm;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Card) {
            Card card = (Card)o;
            return id.equals(card.id);
        }

        return false;
    }
}
