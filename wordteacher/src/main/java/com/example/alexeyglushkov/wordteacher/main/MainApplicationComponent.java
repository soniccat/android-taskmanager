package com.example.alexeyglushkov.wordteacher.main;

import com.example.alexeyglushkov.cachemanager.disk.DiskStorage;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class})
public interface MainApplicationComponent {
    SubMainComponent getSubComponent(MainApplicationModule module);
}
