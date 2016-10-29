package ui;

import android.content.Context;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.alexeyglushkov.streamlib.progress.ProgressInfo;
import com.example.alexeyglushkov.streamlib.progress.ProgressListener;
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
    private boolean isLoading;

    //private ProgressListener progressListener;
    private StartListener startListener;
    private CancelListener cancelListener;

    public LoadingButton(Context context) {
        super(context);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.loading_button, this);

        icon = (ImageView)findViewById(R.id.icon);
        cancelIcon = (ImageView)findViewById(R.id.cancel_icon);
        progress = (ProgressBar)findViewById(R.id.progress);

        bindClickListener();
    }

    private void bindClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLoading) {
                    if (cancelListener != null) {
                        cancelListener.onCancel();
                    }
                } else {
                    if (startListener != null) {
                        startListener.onStart();
                    }
                }
            }
        });
    }

    //// Events

    private void onProgress(ProgressInfo progressInfo) {
        float currentValue = progressInfo.getNormalizedValue();
        if (currentValue != -1.0f) {
            if (progress.isIndeterminate()) {
                progress.setIndeterminate(false);
                progress.setSecondaryProgress(100);
            }

            progress.setProgress((int) (100 * currentValue));
            Log.d("test", "progress " + currentValue);

            if (currentValue == 1.0f) {
                stopLoading();
            }
        }
    }

    private void onCancel() {
        stopLoading();
    }

    public void stopLoading() {
        isLoading = false;
        hideLoading();
    }

    //// Actions

    public ProgressListener startLoading() {
        showLoading();

        isLoading = true;
        progress.setIndeterminate(true);
        progress.setMax(100);

        return new ProgressListener() {
            @Override
            public void onProgressChanged(Object sender, ProgressInfo progressInfo) {
                if (progressInfo.isCancelled()) {
                    onCancel();

                } else {
                    onProgress(progressInfo);
                }
            }
        };
    }

    // Updating UI

    private void showLoading() {
        progress.setVisibility(View.VISIBLE);
        icon.setVisibility(View.INVISIBLE);
        cancelIcon.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progress.setVisibility(View.INVISIBLE);
        icon.setVisibility(View.VISIBLE);
        cancelIcon.setVisibility(View.INVISIBLE);
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

    //// Setters

    public void setStartListener(StartListener startListener) {
        this.startListener = startListener;
    }

    public void setCancelListener(CancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    //// Getters

    public boolean isLoading() {
        return isLoading;
    }

    // States

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

    //// Interfaces

    public interface StartListener {
        void onStart();
    }

    public interface CancelListener {
        void onCancel();
    }
}
