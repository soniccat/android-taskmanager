package com.example.alexeyglushkov.quizletservice.entities;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 27.03.16.
 */
public class QuizletUser implements Serializable {
    private static final long serialVersionUID = 4254004416559222553L;

    private long id;
    private String name;
    private String type;
    private String imageUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
