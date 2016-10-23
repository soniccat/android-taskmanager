package com.example.alexeyglushkov.streamlib.progress;

/**
 * Created by alexeyglushkov on 25.01.15.
 */

// TODO: implement calling listeners depending on how much time passed
public class ProgressUpdater implements ProgressInfo {
    private float contentSize;
    private float progressMinChange; // from 0 to 1
    private float currentValue;
    private float lastTriggeredValue;

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
        if (progressMinChange == 0 || currentValue == contentSize || isSignificantChange) {
            lastTriggeredValue = currentValue;

            if (this.listener != null) {
                this.listener.onProgressUpdated(this);
            }
        }
    }

    public void cancel(Object info) {
        if (this.listener != null) {
            this.listener.onProgressCancelled(this, info);
        }
    }

    public void setContentSize(float contentSize) {
        this.contentSize = contentSize;
    }

    public interface ProgressUpdaterListener {
        void onProgressUpdated(ProgressUpdater updater);
        void onProgressCancelled(ProgressUpdater updater, Object info);
    }
}
