package com.example.alexeyglushkov.authorization;

import android.app.Activity;
import android.content.Intent;

import com.example.alexeyglushkov.authorization.OAuth.OAuthWebClient;

import org.junit.Assert;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;

/**
 * Created by alexeyglushkov on 25.11.15.
 */
public class AuthActivityProxy implements OAuthWebClient {
    private static WeakReference<Activity> currentActivity;

    private static ObservableEmitter<String> emitter;
    private static @NonNull Observable<String> authResult = Observable.create(new ObservableOnSubscribe<String>() {
        @Override
        public void subscribe(ObservableEmitter<String> emitter) throws Exception {
            AuthActivityProxy.emitter = emitter;
        }
    });
    //private static Callback currentCallback;

    public static Activity getCurrentActivity() {
        return currentActivity != null ? currentActivity.get() : null;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        AuthActivityProxy.currentActivity = new WeakReference<>(currentActivity);
    }

    public static @Nullable Single<String> getAuthResult() {
        return Single.fromObservable(authResult);
    }

    public static void finish(String url, Error error) {
        if (error != null) {
            emitter.onError(error);

        } else {
            emitter.onNext(url);
            emitter.onComplete();
        }
    }

    @Override
    public Single<String> loadUrl(String url, String callback) {
        Assert.assertNotNull(currentActivity);

        Intent intent = new Intent(getCurrentActivity(), AuthorizationActivity.class);
        intent.putExtra(AuthorizationActivity.LOAD_URL, url);
        intent.putExtra(AuthorizationActivity.CALLBACK_URL, callback);

        getCurrentActivity().startActivity(intent);
        return getAuthResult();
    }
}
