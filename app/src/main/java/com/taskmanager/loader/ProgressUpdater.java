package com.taskmanager.loader;

/**
 * Created by alexeyglushkov on 25.01.15.
 */
public class ProgressUpdater implements ProgressInfo{
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
        return currentValue/contentSize;
    }

    public void append(float change) {
        currentValue += change;

        if (progressMinChange == 0 || currentValue == contentSize || (currentValue - lastTriggeredValue) / contentSize > progressMinChange) {
            lastTriggeredValue = currentValue;

            if (this.listener != null) {
                this.listener.onProgressUpdated(this);
            }
        }
    }

    public void setContentSize(float contentSize) {
        this.contentSize = contentSize;
    }

    public interface ProgressUpdaterListener {
        void onProgressUpdated(ProgressUpdater updater);
    }
}
