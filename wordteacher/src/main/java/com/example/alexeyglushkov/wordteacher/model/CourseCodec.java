package com.example.alexeyglushkov.wordteacher.model;

import com.example.alexeyglushkov.streamlib.codecs.SimpleCodec;

/**
 * Created by alexeyglushkov on 08.05.16.
 */
public class CourseCodec extends SimpleCodec {
    public CourseCodec() {
        super(new CourseWriter(), new CourseReader());
    }
}
