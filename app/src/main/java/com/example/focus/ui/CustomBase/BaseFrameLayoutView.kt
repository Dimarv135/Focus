package com.example.focus.ui.CustomBase

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.Nullable

abstract class BaseFrameLayoutView : FrameLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        onSetup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        onSetup(context, attrs)
    }

    private fun onSetup(context: Context?, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(getLayout(), this, true)
    }

    abstract fun getLayout(): Int

    abstract fun initView(attrs: AttributeSet?)
}