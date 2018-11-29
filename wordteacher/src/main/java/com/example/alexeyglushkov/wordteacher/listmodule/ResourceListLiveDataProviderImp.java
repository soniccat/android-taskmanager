package com.example.alexeyglushkov.wordteacher.listmodule;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.alexeyglushkov.quizletservice.Resource;
import com.example.alexeyglushkov.quizletservice.ResourceLiveDataProvider;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

public class ResourceListLiveDataProviderImp<T> implements StorableResourceListLiveDataProvider<T> {
    private @NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider;
    private @NonNull Filter<T> filter = new EmptyFilter<T>();

    private @NonNull Resource<List<T>> aResource = new Resource<>();
    private @NonNull List<T> aList = new ArrayList<>();

    public ResourceListLiveDataProviderImp(Bundle bundle, @NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
        this.resourceLiveDataProvider = resourceLiveDataProvider;
        restore(bundle);
    }

    public ResourceListLiveDataProviderImp(@NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
        this.resourceLiveDataProvider = resourceLiveDataProvider;
    }

    public void setFilter(@Nullable Filter<T> filter) {
        this.filter = filter != null ? filter : new EmptyFilter<T>();
    }

    @Override
    public void store(Bundle bundle) {
        bundle.putParcelable("filter", filter);
    }

    @Override
    public void restore(Bundle bundle) {
        filter = bundle.getParcelable("filter");
    }

    @Override
    public LiveData<Resource<List<T>>> getListLiveData() {
        return Transformations.map(resourceLiveDataProvider.getLiveData(), new Function<Resource<List<T>>, Resource<List<T>>>() {
            @Override
            public Resource<List<T>> apply(Resource<List<T>> input) {
                Resource<List<T>> result;

                if (input.data == null) {
                    return aResource.update(input.state, Collections.<T>emptyList(), input.error);

                } else {
                    result = getFilteredList(input);
                }

                return result;
            }
        });
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

    public interface Filter<T> extends Parcelable {
        boolean check(T value);
    }

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
