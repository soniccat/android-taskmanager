package com.example.alexeyglushkov.taskmanager.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by alexeyglushkov on 20.09.15.
 */
public class SortedList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 3697965950633119964L;

    private Comparator<T> comparator;

    public SortedList(Comparator<T> aComparator) {
        comparator = aComparator;
    }

    public void addInSortedOrder(T elem) {
        int insertIndex = Collections.binarySearch(this, elem, comparator);
        if (insertIndex < 0) {
            insertIndex = -insertIndex - 1;
        } else {
            ++insertIndex;
            for (int i=insertIndex; i<size(); ++i) {
                if (comparator.compare(get(i), elem) != 0) {
                    break;
                }
            }
        }

        add(insertIndex, elem);
    }

    public void updateSortedOrder() {
        Collections.sort(this, comparator);
    }
}
