package com.taskmanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class TaskBarView extends View {
    Paint paint;
    ArrayList<TaskBarItem> items;

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
        items = new ArrayList<TaskBarItem>();
    }

    public void clearItems() {
        items.clear();
        postInvalidate();
    }

    public void addItem(TaskBarItem item) {
        items.add(item);
        postInvalidate();
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

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        canvas.drawRect(paddingLeft,paddingTop,paddingLeft + contentWidth, paddingTop + contentHeight, paint);

        int left = paddingLeft;
        for (TaskBarItem item : items) {
            int len = (int)((float)contentWidth * item.length);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(item.color);
            canvas.drawRect(left, paddingTop, left + len, paddingTop + contentHeight, paint);

            if (item.highlight) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.RED);
                canvas.drawRect(left, paddingTop, left + len, paddingTop + contentHeight, paint);
            }

            left += len;
        }
    }

    public static class TaskBarItem {
        int type;
        float length;
        int color;
        boolean highlight;

        public TaskBarItem(int type, float length, int color, boolean highlight) {
            this.type = type;
            this.length = length;
            this.color = color;
            this.highlight = highlight;
        }
    }
}
