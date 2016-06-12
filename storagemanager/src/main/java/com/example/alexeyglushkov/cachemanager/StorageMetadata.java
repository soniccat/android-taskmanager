package com.example.alexeyglushkov.cachemanager;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public interface StorageMetadata extends Serializable {
    void setCreateTime(long createTime);
    long getCreateTime();

    void setExpireTime(long expireTime);
    long getExpireTime();

    void setContentSize(long size);
    long getContentSize();

    void setEntryClass(Class cl);
    Class getEntryClass();
}
