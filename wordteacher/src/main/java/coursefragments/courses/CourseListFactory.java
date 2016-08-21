package coursefragments.courses;

import android.os.Bundle;

import junit.framework.Assert;

import java.util.List;

import listfragment.StorableListProvider;
import listfragment.StorableListProviderFactory;
import model.Course;
import model.CourseHolder;

/**
 * Created by alexeyglushkov on 21.08.16.
 */
public class CourseListFactory implements StorableListProviderFactory<Course> {
    private CourseHolder holder;

    //// Initialization
    public CourseListFactory(CourseHolder holder) {
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
