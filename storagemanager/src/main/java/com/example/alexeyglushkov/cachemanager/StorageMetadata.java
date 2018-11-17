package com.example.alexeyglushkov.cachemanager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public interface StorageMetadata {
    void setCreateTime(long createTime);
    long getCreateTime();

    void setExpireTime(long expireTime);
    long getExpireTime();
    boolean isExpired();

    void setContentSize(long size);
    long getContentSize();

    void setEntryClass(@NonNull Class cl);
    @Nullable Class getEntryClass();
}
