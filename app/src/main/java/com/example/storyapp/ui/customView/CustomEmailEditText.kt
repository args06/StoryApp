package com.example.storyapp.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import com.example.storyapp.R
import com.example.storyapp.utils.FormValidation
import com.google.android.material.textfield.TextInputEditText

class CustomEmailEditText : TextInputEditText {

    private var isEmailNotValid = false
    private var isEmailBlank = false

    var isFormValid = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                if (email.isNotEmpty()) {
                    isEmailNotValid = !FormValidation.isEmailValid(email)
                    isEmailBlank = false
                } else {
                    isEmailBlank = email.isEmpty()
                    isEmailNotValid = false
                }

                isFormValid = !isEmailNotValid && !isEmailBlank

                error = if (isEmailNotValid) context.getString(R.string.incorrect_email_format)
                else if (isEmailBlank) context.getString(R.string.form_empty_message)
                else null
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }
}