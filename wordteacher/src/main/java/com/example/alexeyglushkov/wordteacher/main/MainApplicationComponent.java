package com.example.alexeyglushkov.wordteacher.main;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MainApplicationModule.class)
public interface MainApplicationComponent {
    DiskStorage getStorage();
}
