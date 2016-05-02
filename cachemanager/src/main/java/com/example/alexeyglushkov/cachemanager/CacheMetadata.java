package com.example.alexeyglushkov.cachemanager;

import java.io.Serializable;

/**
 * Created by alexeyglushkov on 02.05.16.
 */
public interface CacheMetadata extends Serializable {
    void setCreateTime(long createTime);
    void setExpireTime(long expireTime);
    void setContentSize(long size);
    void setEntryClass(Class cl);
}
