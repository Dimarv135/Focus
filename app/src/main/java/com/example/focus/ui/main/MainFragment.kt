package com.example.focus.ui.main

import android.graphics.Rect
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.focus.R
import com.example.focus.Utils.Utils
import kotlinx.android.synthetic.main.main_fragment.*
import java.lang.Math.abs

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
checkForKeyboard()
        text1.setCustomHint("Number")
        text1.setInputType("Number")
        text2.setCustomHint("Text")
        main.setOnTouchListener { view, motionEvent ->
            main.requestFocus()
            view.post { Utils().closeKeyboard(main) }

        }
    }

    private fun checkForKeyboard() {
        view?.viewTreeObserver?.addOnGlobalLayoutListener {
            val rect = Rect().apply { view?.getWindowVisibleDisplayFrame(this) }
            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

            val screenHeightDisplayMetrics = displayMetrics.heightPixels

            // rect.bottom is the position above soft keypad or device button.
            // if keypad is shown, the rect.bottom is smaller than that before.
            val diffHeight = abs(screenHeightDisplayMetrics - (rect.bottom ?: 0))

            // 0.15 ratio is perhaps enough to determine keypad screenHeightDisplayMetrics.
            if (diffHeight > screenHeightDisplayMetrics * 0.15) {

                Toast.makeText(activity, "OPEN", Toast.LENGTH_LONG).show()
            } else  {
                Toast.makeText(activity, "CLOSED", Toast.LENGTH_LONG).show()
            }

        }
    }

}
