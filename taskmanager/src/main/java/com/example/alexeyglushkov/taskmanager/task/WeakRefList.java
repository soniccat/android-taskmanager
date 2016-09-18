package com.example.alexeyglushkov.taskmanager.task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by alexeyglushkov on 23.08.15.
 */
public class WeakRefList<T> extends ArrayList<WeakReference<T>> {

    private static final long serialVersionUID = 4962762847646156417L;

    @Override
    public Iterator<WeakReference<T>> iterator() {
        clean();
        return super.iterator();
    }

    public void clean() {
        for (int i = this.size(); --i >= 0;) {
            if (get(i).get() == null) {
                remove(i);
            }
        }
    }

    @Override
    public boolean contains(Object object) {
        if (object instanceof WeakReference) {
            return super.contains(object);
        }

        for (WeakReference<T> ref : this) {
            if (ref.get() == object) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean remove(Object object) {
        if (object instanceof WeakReference) {
            return super.remove(object);
        }

        int i = 0;
        for (WeakReference<T> ref : this) {
            if (ref.get() == object) {
                remove(i);
                return true;
            }

            ++i;
        }

        return false;
    }
}
