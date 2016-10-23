package ui;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alexeyglushkov.wordteacher.R;

/**
 * Created by alexeyglushkov on 23.10.16.
 */

public class LoadingButton extends FrameLayout {
    static final int ANIM_STATE_NONE = 0;
    static final int ANIM_STATE_HIDING = 1;
    static final int ANIM_STATE_SHOWING = 2;

    int mAnimState = ANIM_STATE_NONE;

    private ImageView icon;
    private ImageView cancelIcon;
    private ProgressBar progress;

    public LoadingButton(Context context) {
        super(context);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.loading_button, this);

        icon = (ImageView)findViewById(R.id.icon);
        cancelIcon = (ImageView)findViewById(R.id.cancel_icon);
        progress = (ProgressBar)findViewById(R.id.progress);
    }

    public void hide() {
        if (isOrWillBeHidden()) {
            return;
        }

        clearAnimation();

        mAnimState = ANIM_STATE_HIDING;

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_zoom_out);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimState = ANIM_STATE_NONE;
                setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(anim);
    }

    public void show() {
        if (isOrWillBeShown()) {
            return;
        }

        clearAnimation();

        mAnimState = ANIM_STATE_SHOWING;

        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fab_zoom_in);
        anim.setInterpolator(new FastOutLinearInInterpolator());
        anim.setDuration(200);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAnimState = ANIM_STATE_NONE;
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation(anim);
    }

    private boolean isOrWillBeShown() {
        if (this.getVisibility() != View.VISIBLE) {
            // If we not currently visible, return true if we're animating to be shown
            return mAnimState == ANIM_STATE_SHOWING;
        } else {
            // Otherwise if we're visible, return true if we're not animating to be hidden
            return mAnimState != ANIM_STATE_HIDING;
        }
    }

    private boolean isOrWillBeHidden() {
        if (this.getVisibility() == View.VISIBLE) {
            // If we currently visible, return true if we're animating to be hidden
            return mAnimState == ANIM_STATE_HIDING;
        } else {
            // Otherwise if we're not visible, return true if we're not animating to be shown
            return mAnimState != ANIM_STATE_SHOWING;
        }
    }
}
