package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.dropboxservice.ObjectMerger;

/**
 * Created by alexeyglushkov on 11.09.16.
 */
public class CourseMerger implements ObjectMerger {
    public Object merge(Object obj1, Object obj2) {
        Course course1 = (Course)obj1;
        Course course2 = (Course)obj2;

        Course result = new Course(course1);
        for (Card card : course2.getCards()) {
            if (result.getCard(card.getId()) == null) {
                result.addCard(card);
            }
        }

        return result;
    };
}
