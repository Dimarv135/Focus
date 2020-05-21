package com.example.focus.ui.CustomBase

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.focus.R
import com.example.focus.Utils.Utils
import com.example.focus.extensions.addRipple
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.view_text_input.view.*

class CustomTextInputLayout(context: Context, attributeSet: AttributeSet) : BaseFrameLayoutView(context, attributeSet) {

    override fun getLayout() = R.layout.view_text_input

    private var mListener: ActionEditListener? = null
    private var mIconListener: IconClickListener? = null
    private var mFocusListener: TextInputFocusListener? = null

    private var isValid: Boolean = true
    private var isPassVisible: Boolean = false

    override fun initView(attrs: AttributeSet?) {
        val attributeArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout)
        setCustomHint(attributeArray.getString(R.styleable.CustomTextInputLayout_hint))
        setDrawableRight(attributeArray.getDrawable(R.styleable.CustomTextInputLayout_drawableRight))
        setInputType(attributeArray.getString(R.styleable.CustomTextInputLayout_inputType))
        setMaxLength(attributeArray.getString(R.styleable.CustomTextInputLayout_maxLength))
        setDrawableClickable(attributeArray.getBoolean(R.styleable.CustomTextInputLayout_drawableClickable, true))
        setDrawableFocusable(attributeArray.getBoolean(R.styleable.CustomTextInputLayout_drawableFocusable, true))
        setText("")
        customTextInputViewEdit?.setPadding(Utils().convertDpToPixel(16), 0, Utils().convertDpToPixel(16), Utils().convertDpToPixel(4))
        customTextInputViewEdit?.addTextChangedListener(getInputTextWatcher(customTextInputViewLayout))

        customTextInputViewEdit?.onFocusChangeListener = OnFocusChangeListener { view, focus ->
            if (focus) {
                mFocusListener?.onFocusChangeListener()
                customTextInputViewEdit?.setPadding(Utils().convertDpToPixel(16), Utils().convertDpToPixel(6), Utils().convertDpToPixel(16), 0)
                Utils().openKeyboard(view)
            } else if (getText().isEmpty()) {
                customTextInputViewEdit?.setPadding(Utils().convertDpToPixel(16), 0, Utils().convertDpToPixel(16), Utils().convertDpToPixel(4))
            }
            refreshDrawableState()
        }

        customTextInputViewEdit?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mListener?.onActionEditListener()
                true
            } else {
                false
            }
        }

        textInputViewIcon.setOnClickListener {
            mIconListener?.onIconClickListener()
        }

        //disable long click
        customTextInputViewEdit?.isLongClickable = attributeArray.getBoolean(R.styleable.CustomTextInputLayout_android_longClickable, true)


        attributeArray.recycle()
    }

    /**
     * Text to text input and edit text views, related methods
     */
    private fun clearEditTextColorfilter() {
        val editText = customTextInputViewEdit
        if (editText != null) {
            val background = editText.background
            background?.clearColorFilter()
        }
    }

    fun setEditTextUnfocusable() {
        customTextInputViewEdit?.isClickable = false
        customTextInputViewEdit?.isFocusable = false
    }

    fun setText(text: String?) {
        if (text == null)
            return

        if (text.trim().isNotEmpty())
            setLayoutActivated()

        customTextInputViewEdit?.setText(text.trim())
        customTextInputViewEdit?.clearFocus()
    }

    fun getText(): String {
        return customTextInputViewEdit?.text.toString().trim()
    }

    fun getTextEditView(): EditText {
        return customTextInputViewEdit
    }

    fun setTextInputBackgroundColor(color: Int) {
        //inputContainerView?.setBackgroundColor(ContextCompat.getColor(context,color))
        clearEditTextColorfilter()
        customTextInputViewLayout?.setHintTextAppearance(R.style.TextInputHintError)
        customTextInputViewLayout?.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        customTextInputViewLayout?.hintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))

        inputContainerView?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        customTextInputViewEdit?.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun setCustomHint(hint: String?) {
        customTextInputViewLayout.hint = hint
    }

    fun setHintColor(color: Int) {
        customTextInputViewEdit?.setHintTextColor(color)
        // customTextInputViewEdit?.hintTextColor = ColorStateList.valueOf(resources.getColor(color))
    }

    fun setLayoutActivated() {
        customTextInputViewEdit?.setPadding(Utils().convertDpToPixel(16), Utils().convertDpToPixel(6), Utils().convertDpToPixel(16), 0)
    }

    //TODO: is safer to be enum(the attribute options)
    fun setInputType(inputType: String?) {
        if (inputType != null && inputType.contains("number", true)) {
            customTextInputViewEdit?.inputType = InputType.TYPE_CLASS_NUMBER

        } else if (inputType.equals("email")) {
            customTextInputViewEdit?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        } else {
            customTextInputViewEdit?.inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    fun setInputTypeFocusWithOpenKeyboard() {
        Utils().openKeyboard(customTextInputViewEdit)
    }

    fun setAsEmailField() {
        customTextInputViewEdit?.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    }

    fun setImeOptions(imeOption: Int) {
        customTextInputViewEdit?.imeOptions = imeOption
    }

    fun isEditable(canBeEdit: Boolean) {
        customTextInputViewEdit.isEnabled = canBeEdit
    }

    fun setMaxLength(maxLength: String?) {
        if (maxLength == null)
            return

        val fArray = arrayOfNulls<InputFilter>(1)
        fArray[0] = InputFilter.LengthFilter(maxLength.toInt())
        customTextInputViewEdit?.filters = fArray
    }

    /**
     * On Edit text CLick listener
     */
    fun onClick(listener: OnClickListener) {
        customTextInputViewEdit?.setOnClickListener(listener)
        textInputViewIcon?.setOnClickListener(listener)
    }

    /**
     * Text watcher for text input layout
     */
    private fun getInputTextWatcher(inputLayout: TextInputLayout): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                inputLayout.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable) {

            }
        }
    }

    /**
     * Setter for custom Listeners
     */
    fun setOnActionEditListener(listener: ActionEditListener) {
        mListener = listener
    }

    fun setOnRightIconClickListener(listener: IconClickListener) {
        mIconListener = listener
    }

    fun setOnFocusChangeListener(listener: TextInputFocusListener) {
        mFocusListener = listener
    }


    /**
     * Error related methods
     */
    fun clearError() {
        isValid = true
        clearEditTextColorfilter()
        customTextInputViewLayout?.setHintTextAppearance(R.style.TextInputHintActive)

        customTextInputViewLayout?.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))
        customTextInputViewLayout?.hintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.grey))

        inputContainerView?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.greyLighter))
        customTextInputViewEdit?.setBackgroundColor(ContextCompat.getColor(context, R.color.greyLighter))

        //hide bottom text message on error View
        extInputErrorBottomMessage?.visibility= View.GONE
    }

    fun setEmptyError() {
        isValid = false
        clearEditTextColorfilter()
        customTextInputViewLayout?.setHintTextAppearance(R.style.TextInputHintError)
        customTextInputViewLayout?.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        customTextInputViewLayout?.hintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))

        inputContainerView?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.redLight))
        customTextInputViewEdit?.setBackgroundColor(ContextCompat.getColor(context, R.color.redLight))

        customTextInputViewEdit?.setSelection(customTextInputViewEdit.text?.length ?: 0)
    }

    fun setErrorWithBottomMessage(errorMessage: String?) {
        setEmptyError()
        extInputErrorBottomMessage.visibility= View.VISIBLE
        extInputErrorBottomMessage?.text = errorMessage
    }

    /**
     * Drawable related methods
     */
    fun setDrawableRight(icon: Drawable?) {
        textInputViewIcon?.setImageDrawable(icon)
        if (icon != null && textInputViewIcon.visibility != View.VISIBLE) {
            textInputViewIcon.visibility= View.VISIBLE
        }

        if (icon == null) {
            textInputViewIcon.visibility= View.GONE
        }
    }

    fun setDrawableClickable(isClickable : Boolean) {
        textInputViewIcon?.isClickable = isClickable

        if(isClickable) {
            textInputViewIcon?.addRipple()
        }
    }

    fun setDrawableFocusable(isFocusable : Boolean) {
        textInputViewIcon?.isFocusable = isFocusable
    }

    fun setClearTextDrawable() {
        setDrawableRight(ContextCompat.getDrawable(context, android.R.drawable.ic_menu_close_clear_cancel))
        textInputViewIcon?.addRipple()
        textInputViewIcon.setOnClickListener {
            customTextInputViewEdit?.text?.clear()
            customTextInputViewEdit?.requestFocus()
        }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (customTextInputViewEdit != null) {
            if (isValid) {
                clearError()
            } else {
                setEmptyError()
            }
        }
    }

    fun getIconView(): ImageView {
        return textInputViewIcon
    }

    fun setCloseIconVisiblity(show: Boolean) {
        textInputViewIcon.visibility= if (show) View.VISIBLE else View.GONE
    }

    /**
     * Password related methods
     */
    var passwordVisibilityIcon: Int = android.R.drawable.ic_lock_lock
    fun setAsPasswordField() {

        RxTextView.textChanges(getTextEditView())
                .subscribe { text ->
                    if (text.isNotEmpty()) {
                        setDrawableRight(ContextCompat.getDrawable(context, passwordVisibilityIcon))
                    } else if (text.isEmpty()) {
                        setDrawableRight(null)
                    }
                }


        customTextInputViewEdit.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (isPassVisible) passwordInvisible()
        }
        customTextInputViewEdit?.transformationMethod = PasswordTransformationMethod.getInstance()
        customTextInputViewEdit?.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        textInputViewIcon?.setOnClickListener {
            if (customTextInputViewEdit.transformationMethod.javaClass.simpleName.contains("PasswordTransformationMethod")) {
                passwordVisible()
            } else {
                passwordInvisible()
            }

            customTextInputViewEdit?.setSelection(customTextInputViewEdit.text?.length!!)
        }
    }

    private fun passwordVisible() {
        isPassVisible = true
        passwordVisibilityIcon = android.R.drawable.ic_lock_lock
        setDrawableRight(ContextCompat.getDrawable(context, passwordVisibilityIcon))
        customTextInputViewEdit?.transformationMethod = SingleLineTransformationMethod.getInstance()
    }

    private fun passwordInvisible() {
        isPassVisible = false
        passwordVisibilityIcon = android.R.drawable.ic_lock_idle_lock
        setDrawableRight(ContextCompat.getDrawable(context, passwordVisibilityIcon))
        customTextInputViewEdit?.transformationMethod = PasswordTransformationMethod.getInstance()

    }

    /**
     * Custom Listeners
     */
    interface ActionEditListener {
        fun onActionEditListener()
    }

    interface IconClickListener {
        fun onIconClickListener()
    }

    interface TextInputFocusListener {
        fun onFocusChangeListener()
    }
}