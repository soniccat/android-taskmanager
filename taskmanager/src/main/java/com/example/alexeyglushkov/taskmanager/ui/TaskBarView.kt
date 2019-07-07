package com.example.alexeyglushkov.taskmanager.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import java.util.ArrayList

/**
 * TODO: document your custom view class.
 */
class TaskBarView : View {
    protected lateinit var paint: Paint
    protected lateinit var items: ArrayList<TaskBarItem>

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        /*
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TaskBarView, defStyle, 0);

        a.recycle();
        */

        paint = Paint()
        items = ArrayList()
    }

    fun clearItems() {
        items.clear()
        postInvalidate()
    }

    fun addItem(item: TaskBarItem) {
        items.add(item)
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        val paddingRight = paddingRight
        val paddingBottom = paddingBottom

        val contentWidth = width - paddingLeft - paddingRight
        val contentHeight = height - paddingTop - paddingBottom

        paint.style = Paint.Style.STROKE
        paint.color = Color.BLACK
        paint.strokeWidth = 4f
        canvas.drawRect(paddingLeft.toFloat(), paddingTop.toFloat(), (paddingLeft + contentWidth).toFloat(), (paddingTop + contentHeight).toFloat(), paint)

        var left = paddingLeft
        for (item in items) {
            val len = (contentWidth.toFloat() * item.length).toInt()

            paint.style = Paint.Style.FILL
            paint.color = item.color
            canvas.drawRect(left.toFloat(), paddingTop.toFloat(), (left + len).toFloat(), (paddingTop + contentHeight).toFloat(), paint)

            if (item.highlight) {
                paint.style = Paint.Style.STROKE
                paint.color = Color.RED
                canvas.drawRect(left.toFloat(), paddingTop.toFloat(), (left + len).toFloat(), (paddingTop + contentHeight).toFloat(), paint)
            }

            left += len
        }
    }

    class TaskBarItem(internal var type: Int, internal var length: Float, internal var color: Int, internal var highlight: Boolean)
}
