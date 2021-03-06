package com.example.alexeyglushkov.streamlib.progress;

/**
 * Created by alexeyglushkov on 25.01.15.
 */

// TODO: implement calling listeners depending on how much time passed
public class ProgressUpdater implements ProgressInfo {
    // in
    private float contentSize;
    private float progressMinChange; // from 0 to 1

    // out
    private float currentValue;
    private float lastTriggeredValue;
    private boolean isCancelled;
    private boolean isFinished;

    ProgressUpdaterListener listener;

    public ProgressUpdater(float contentSize, float progressMinChange, ProgressUpdaterListener listener) {
        this.contentSize = contentSize;
        this.progressMinChange = progressMinChange;
        this.listener = listener;
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public float getNormalizedValue() {
        return contentSize != 0 ? currentValue/contentSize : -1.0f;
    }

    public void append(float change) {
        currentValue += change;

        boolean isSignificantChange = (currentValue - lastTriggeredValue) / contentSize > progressMinChange;
        if (currentValue > contentSize) {
            currentValue = contentSize;
        }

        if (progressMinChange == 0 || currentValue == contentSize || isSignificantChange) {
            lastTriggeredValue = currentValue;

            synchronized (this) {
                if (this.listener != null) {
                    this.listener.onProgressUpdated(this);
                }
            }
        }
    }

    public void cancel(Object info) {
        isCancelled = true;

        // cancel could be called from the thread of a task manager
        // finish and append are called from the thread of a task
        synchronized (this) {
            if (this.listener != null) {
                this.listener.onProgressCancelled(this, info);
                this.listener = null;
            }
        }
    }

    public void finish() {
        isFinished = true;

        synchronized (this) {
            if (this.listener != null) {
                this.listener.onProgressUpdated(this);
            }
        }
    }


    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    public void setContentSize(float contentSize) {
        this.contentSize = contentSize;
    }

    public void clear() {
        currentValue = 0;
        lastTriggeredValue = 0;
        isCancelled = false;
        isFinished = false;
    }

    public interface ProgressUpdaterListener {
        void onProgressUpdated(ProgressUpdater updater);
        void onProgressCancelled(ProgressUpdater updater, Object info);
    }
}
