package tools;

import androidx.annotation.Nullable;
import io.reactivex.Single;

public class RxTools {
    public static <T> Single<T> justOrError(@Nullable final T item) {
        return item != null ? Single.just(item) : Single.<T>error(new Error("not authorized"));
    }
}
