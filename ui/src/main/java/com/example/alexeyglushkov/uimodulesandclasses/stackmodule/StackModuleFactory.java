package com.example.alexeyglushkov.uimodulesandclasses.stackmodule;

/**
 * Created by alexeyglushkov on 04.11.16.
 */

public interface StackModuleFactory {
    StackModuleItem rootModule(StackModule stackModule);
    StackModuleItem moduleFromObject(Object object, StackModule stackModule);
    StackModuleItem restoreModule(Object viewObject, StackModule stackModule);
}
