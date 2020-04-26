package com.aglushkov.wordteacher.features.definitions.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.aglushkov.general.extensions.pxToDp
import com.aglushkov.general.extensions.resolveThemeStyle
import com.aglushkov.general.extensions.setTextAppearanceCompat
import com.aglushkov.wordteacher.R
import com.aglushkov.wordteacher.features.definitions.adapter.DefinitionsAdapter

class WordTitleView: LinearLayout {
    val title: TextView
    val providedBy: TextView

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        applyAttributeSet(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyAttributeSet(attrs)
    }

    init {
        orientation = HORIZONTAL
        isBaselineAligned = false

        title = TextView(context).apply {
            setTextAppearanceCompat(context.resolveThemeStyle(R.attr.wordTitleTextAppearance))
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                weight = 1.0f
            }
        }

        providedBy = TextView(context).apply {
            maxWidth = context.resources.getDimensionPixelSize(R.dimen.word_providedBy_maxWidth)
            textAlignment = TEXT_ALIGNMENT_TEXT_END
            setTextAppearanceCompat(context.resolveThemeStyle(R.attr.wordProvidedByTextAppearance))
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.TOP
            }
        }

        addView(title)
        addView(providedBy)

        DefinitionsAdapter.setWordHorizontalPadding(this)
    }

    private fun applyAttributeSet(attrs: AttributeSet?) {
    }
}