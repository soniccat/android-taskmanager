package com.example.alexeyglushkov.wordteacher;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class AppBarFloatingActionBehavior extends FloatingActionButton.Behavior {
    private int prevTop = -1;

    public AppBarFloatingActionBehavior(Context context, AttributeSet attributeSet){
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        boolean result = super.layoutDependsOn(parent, child, dependency);
        return result || dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        boolean result = super.onDependentViewChanged(parent, child, dependency);
        if (dependency instanceof AppBarLayout) {
            handleAppBarChanges(child, dependency);
        }

        return result;
    }

    private void handleAppBarChanges(FloatingActionButton child, View dependency) {
        if (prevTop == -1) {
            if (dependency.getTop() < 0) {
                // to hide
                prevTop = dependency.getTop() + 1;
            } else {
                // to show
                prevTop = dependency.getTop();
            }
        }

        if (dependency.getTop() < prevTop) {
            child.hide();
        } else {
            child.show();
        }

        prevTop = dependency.getTop();
    }
}