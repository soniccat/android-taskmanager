package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.ResourceLiveDataProvider;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

public class ResourceListLiveDataProviderImp<T> implements StorableResourceListLiveDataProvider<T>, StrategySortable<T> {
    protected @NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider;
    protected @NonNull Filter<T> filter = new EmptyFilter<T>();
    protected @NonNull MutableLiveData<CompareStrategy<T>> compareStrategy = new MutableLiveData<>();

    private @NonNull Resource<List<T>> aResource = new Resource<>();
    private @NonNull List<T> aList = new ArrayList<>();

    //// Initialization / Restoration

    public ResourceListLiveDataProviderImp(@Nullable Bundle bundle, @NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
        this.resourceLiveDataProvider = resourceLiveDataProvider;
        if (bundle != null) {
            restore(bundle);
        }
    }

    public ResourceListLiveDataProviderImp(@NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
        this.resourceLiveDataProvider = resourceLiveDataProvider;
    }

    protected void setResourceLiveDataProvider(@NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
        this.resourceLiveDataProvider = resourceLiveDataProvider;
    }

    @Override
    public void store(Bundle bundle) {
        bundle.putParcelable("filter", filter);
        bundle.putParcelable("compareStrategy", compareStrategy.getValue());
    }

    @Override
    public void restore(Bundle bundle) {
        filter = bundle.getParcelable("filter");
        compareStrategy.setValue((CompareStrategy<T>)bundle.getParcelable("compareStrategy"));
    }

    //// Getters / Setters

    // Setters

    public void setCompareStrategy(@Nullable CompareStrategy<T> compareStrategy) {
        this.compareStrategy.setValue(compareStrategy);
    }

    public void setFilter(@Nullable Filter<T> filter) {
        this.filter = filter != null ? filter : new EmptyFilter<T>();
    }

    // Getters

    @Override
    public LiveData<Resource<List<T>>> getListLiveData() {
        final MediatorLiveData<Resource<List<T>>> result = new MediatorLiveData<>();
        Observer<Resource<List<T>>> resultListObserver = new Observer<Resource<List<T>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<T>> x) {
                result.setValue(new Function<Resource<List<T>>, Resource<List<T>>>() {
                    @Override
                    public Resource<List<T>> apply(Resource<List<T>> input) {
                        return buildFinalResource(input);
                    }
                }.apply(x));
            }
        };

        setupListMediator(result, resultListObserver);
        return result;
    }

    private Resource<List<T>> buildFinalResource(Resource<List<T>> input) {
        Resource<List<T>> result;

        if (input.data == null) {
            result = aResource.update(input.state, Collections.<T>emptyList(), input.error);

        } else {
            result = prepareFinalResource(input);
        }

        return result;
    }

    protected void setupListMediator(MediatorLiveData<Resource<List<T>>> result, Observer<Resource<List<T>>> resultListObserver) {
        result.addSource(resourceLiveDataProvider.getLiveData(), resultListObserver);
        result.addSource(Transformations.map(compareStrategy, new Function<CompareStrategy<T>, Resource<List<T>>>() {
            @Override
            public Resource<List<T>> apply(CompareStrategy<T> input) {
                return resourceLiveDataProvider.getLiveData().getValue(); // might be null
            }
        }), resultListObserver);
    }

    protected Resource<List<T>> prepareFinalResource(Resource<List<T>> input) {
        Resource<List<T>> result;
        result = getFilteredList(input);

        if (compareStrategy.getValue() != null) {
            result = getSortedList(input);
        }
        return result;
    }

    @Override
    public LiveData<Resource<List<T>>> getLiveData() {
        return getListLiveData();
    }

    private Resource<List<T>> getFilteredList(@NonNull Resource<List<T>> input) {
        Assert.assertNotNull(input.data);

        List<T> result;
        aList.clear();
        for (T v : input.data) {
            if (filter.check(v)) {
                aList.add(v);
            }
         }

        result = aList;
        return aResource.update(result);
    }

    private Resource<List<T>> getSortedList(@NonNull Resource<List<T>> input) {
        Assert.assertNotNull(input.data);
        Assert.assertNotNull(this.compareStrategy.getValue());

        aResource.update(input.data);
        Collections.sort(input.data, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return compareStrategy.getValue().compare(o1, o2);
            }
        });

        return input;
    }

    @Nullable
    public CompareStrategy<T> getCompareStrategy() {
        return compareStrategy.getValue();
    }

    //// Inner Interfaces

    public interface Filter<T> extends Parcelable {
        boolean check(T value);
    }

    //// Inner Classes

    public static class EmptyFilter<T> implements Filter<T> {
        public static final Creator<EmptyFilter> CREATOR = new Creator<EmptyFilter>() {
            @Override
            public EmptyFilter createFromParcel(Parcel source) {
                return new EmptyFilter(source);
            }

            @Override
            public EmptyFilter[] newArray(int size) {
                return new EmptyFilter[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public EmptyFilter() {
        }

        protected EmptyFilter(Parcel in) {
        }

        @Override
        public boolean check(T value) {
            return true;
        }
    }
}
