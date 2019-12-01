package tools

import io.reactivex.Single

object RxTools {
    fun <T> justOrError(item: T?): Single<T> {
        return if (item != null) Single.just(item) else Single.error(Error("RxTools: Empty item"))
    }
}