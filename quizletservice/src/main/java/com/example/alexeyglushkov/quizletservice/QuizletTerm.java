package com.example.alexeyglushkov.quizletservice;

/**
 * Created by alexeyglushkov on 02.04.16.
 */
public class QuizletTerm {
    private long id;
    private String term;
    private String definition;
    private int rank;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
