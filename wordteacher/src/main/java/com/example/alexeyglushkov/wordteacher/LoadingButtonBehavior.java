package com.example.alexeyglushkov.wordteacher;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import ui.LoadingButton;

/**
 * Created by alexeyglushkov on 13.06.16.
 */
public class LoadingButtonBehavior extends CoordinatorLayout.Behavior<LoadingButton> implements AppBarLayout.OnOffsetChangedListener {
    private LoadingButton child;

    public LoadingButtonBehavior(Context context, AttributeSet attributeSet){
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LoadingButton child, View dependency) {
        if (dependency instanceof AppBarLayout) {
            this.child = child;

            AppBarLayout lp = (AppBarLayout) dependency;
            lp.addOnOffsetChangedListener(this);
        }

        return false;
    }

    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset < 0) {
            child.hide();

        } else if (verticalOffset == 0) {
            child.show();
        }
    }
}