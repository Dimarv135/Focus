package com.example.focus.Utils

import android.content.Context
import android.content.res.Resources
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Nullable


class Utils {
    public fun openKeyboard(view: View?) {
        view?.let {
            it.requestFocus()
            val imm =
                it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun closeKeyboard(@Nullable view: View?) {
        view?.let {
            closeKeyboard(it.context, it.windowToken)
        }
    }

    private fun closeKeyboard(context: Context?, windowToken: IBinder?) {
        if (context != null && windowToken != null) {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
    public fun convertDpToPixel(dp: Int): Int{
        return dp* Resources.getSystem().displayMetrics.density.toInt()
    }
}