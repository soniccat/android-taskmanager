package com.ga.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.example.rssreader.R;

/**
 * TODO: document your custom view class.
 */
public class TaskBarView extends View {
    Paint paint;

    public TaskBarView(Context context) {
        super(context);
        init(null, 0);
    }

    public TaskBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TaskBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        /*
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TaskBarView, defStyle, 0);

        a.recycle();
        */

        paint = new Paint();
        paint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        canvas.drawRect(paddingLeft,paddingTop,paddingLeft + contentWidth, paddingTop + contentHeight, paint);
    }
}
