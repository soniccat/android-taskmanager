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
public class ToolbarFloatingActionBehavior extends FloatingActionButton.Behavior {
    //private Toolbar toolbar;
    //private int top;
    private int prevTop = -1;

    public ToolbarFloatingActionBehavior(Context context, AttributeSet attributeSet){
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
            /*if (toolbar == null) {
                toolbar = (Toolbar) dependency.findViewById(R.id.main_toolbar);
                top = dependency.getTop();
                prevTop = dependency.getTop();
            }*/

            if (prevTop == -1) {
                prevTop = dependency.getTop();
            }

            if (dependency.getTop() < prevTop) {
                child.hide();
            } else {
                child.show();
            }

            prevTop = dependency.getTop();
            //int dy = top - dependency.getTop();
            //float ratio = (float)dy / (float)toolbar.getHeight();
            //Log.d("TOOLBAR", "" + dependency.getHeight() + " " + dependency.getTop() + " " + toolbar.getHeight() + " " + dy + " " + ratio);
        }

        return result;
    }


}
