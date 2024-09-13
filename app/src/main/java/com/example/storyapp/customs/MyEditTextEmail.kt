package com.example.storyapp.customs

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.example.storyapp.R

class MyEditTextEmail : AppCompatEditText {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int){

            }
            val errormessage = context.getString(R.string.errormessageemail)

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
               if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                   setError(errormessage, null)
                } else {
                    null
                }
            }

            override fun afterTextChanged(s: Editable) {

            }

        })
    }

}