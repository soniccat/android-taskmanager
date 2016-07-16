package com.example.alexeyglushkov.dropboxservice;

import com.example.alexeyglushkov.authorization.Auth.ServiceCommandProvider;
import com.example.alexeyglushkov.service.CachableHttpLoadTask;

/**
 * Created by alexeyglushkov on 10.07.16.
 */
public interface DropboxCommandProvider extends ServiceCommandProvider {
    UploadCommand getUploadCommand(String server, String userId, CachableHttpLoadTask.CacheMode cacheMode);
}
