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

public class ListLiveDataProviderFromResource<T> implements StorableListLiveDataProvider<T> {
    private @NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider;
    private @NonNull Filter<T> filter = new EmptyFilter<T>();
    private @NonNull List<T> filtered = new ArrayList<>();

    public ListLiveDataProviderFromResource(Bundle bundle, Object context) {
        this.resourceLiveDataProvider = (ResourceLiveDataProvider<List<T>>)context;
        restore(bundle, context);
    }

    public ListLiveDataProviderFromResource(@NonNull ResourceLiveDataProvider<List<T>> resourceLiveDataProvider) {
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
    public void restore(Bundle bundle, Object context) {
        filter = bundle.getParcelable("filter");
    }

    @Override
    public LiveData<List<T>> getListLiveData() {
        return Transformations.map(resourceLiveDataProvider.getLiveData(), new Function<Resource<List<T>>, List<T>>() {
            @Override
            public List<T> apply(Resource<List<T>> input) {
                List<T> result;

                if (input.data == null) {
                    return Collections.emptyList();

                } else {
                    result = getFilteredList(input);
                }

                return result;
            }
        });
    }

    private List<T> getFilteredList(@NonNull Resource<List<T>> input) {
        Assert.assertNotNull(input.data);

        List<T> result;
        filtered.clear();
        for (T v : input.data) {
            if (filter.check(v)) {
                filtered.add(v);
            }
         }

        result = filtered;
        return result;
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
