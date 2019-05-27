package com.example.alexeyglushkov.wordteacher.main;

import com.example.alexeyglushkov.cachemanager.Storage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;
import javax.inject.Singleton;

import dagger.Subcomponent;

@ListScope
@Subcomponent(modules = MainApplicationModule.class)
public interface SubMainComponent {
    Storage getStorage();
    void inject(MainApplication app);
}
