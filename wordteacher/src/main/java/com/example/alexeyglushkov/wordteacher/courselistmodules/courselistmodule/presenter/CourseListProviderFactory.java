package com.example.alexeyglushkov.wordteacher.courselistmodules.courselistmodule.presenter;

import android.os.Bundle;

import org.junit.Assert;

import java.util.List;

import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProvider;
import com.example.alexeyglushkov.wordteacher.listmodule.StorableListProviderFactory;
import com.example.alexeyglushkov.wordteacher.model.Course;
import com.example.alexeyglushkov.wordteacher.model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CourseListProviderFactory implements StorableListProviderFactory<Course> {
    private CourseHolder holder;

    //// Initialization
    public CourseListProviderFactory(CourseHolder holder) {
        Assert.assertNotNull(holder);
        this.holder = holder;
    }

    //// Interface methods

    // StorableListProviderFactory
    @Override
    public StorableListProvider<Course> createFromList(List<Course> list) {
        return new CourseListProvider(list);
    }

    @Override
    public StorableListProvider<Course> createFromObject(Object obj) {
        Assert.fail("Not supported");
        return null;
    }

    @Override
    public StorableListProvider<Course> restore(Bundle bundle) {
        StorableListProvider<Course> result = null;

        if (CourseListProvider.canRestore(bundle)) {
            result = new CourseListProvider(bundle, holder);

        } else {
            result = createDefault();
        }

        return result;
    }

    @Override
    public StorableListProvider<Course> createDefault() {
        return new StorableListProvider<Course>() {
            @Override
            public void store(Bundle bundle) {

            }

            @Override
            public void restore(Bundle bundle, Object context) {

            }

            @Override
            public List<Course> getList() {
                return holder.getCourses();
            }
        };
    }
}
