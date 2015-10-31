package org.scribe.model;

import java.util.concurrent.TimeUnit;

/**
 * Created by alexeyglushkov on 24.10.15.
 */
public class TimeoutTuner extends RequestTuner
{
    private final int duration;
    private final TimeUnit unit;

    public TimeoutTuner(int duration, TimeUnit unit)
    {
        this.duration = duration;
        this.unit = unit;
    }

    @Override
    public void tune(Request request)
    {
        request.setReadTimeout(duration, unit);
    }
}
