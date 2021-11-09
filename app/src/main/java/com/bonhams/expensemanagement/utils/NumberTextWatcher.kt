package com.bonhams.expensemanagement.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.ParseException


class NumberTextWatcher(editText: EditText, pattern: String?) : TextWatcher {
    private val df: DecimalFormat
    private val dfnd: DecimalFormat
    private val et: EditText
    private var hasFractionalPart: Boolean
    private var trailingZeroCount = 0
    override fun afterTextChanged(s: Editable) {
        et.removeTextChangedListener(this)
        if (s != null && !s.toString().isEmpty()) {
            try {
                val inilen: Int
                val endlen: Int
                inilen = et.text.length
                val v: String = s.toString().replace(
                    java.lang.String.valueOf(
                        df.getDecimalFormatSymbols().getGroupingSeparator()
                    ), ""
                ).replace("$ ", "")
                val n: Number = df.parse(v)
                val cp = et.selectionStart
                if (hasFractionalPart) {
                    val trailingZeros = StringBuilder()
                    while (trailingZeroCount-- > 0) trailingZeros.append('0')
                    et.setText(df.format(n).toString() + trailingZeros.toString())
                } else {
                    et.setText(dfnd.format(n))
                }
                et.setText("$" + et.text.toString())
                endlen = et.text.length
                val sel = cp + (endlen - inilen)
                if (sel > 0 && sel < et.text.length) {
                    et.setSelection(sel)
                } else if (trailingZeroCount > -1) {
                    et.setSelection(et.text.length - 3)
                } else {
                    et.setSelection(et.text.length)
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        et.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        var index: Int = s.toString()
            .indexOf(java.lang.String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))
        trailingZeroCount = 0
        if (index > -1) {
            index++
            while (index < s.length) {
                if (s[index] == '0') trailingZeroCount++ else {
                    trailingZeroCount = 0
                }
                index++
            }
            hasFractionalPart = true
        } else {
            hasFractionalPart = false
        }
    }

    init {
        df = DecimalFormat(pattern)
        df.setDecimalSeparatorAlwaysShown(true)
        dfnd = DecimalFormat("#,###.00")
        et = editText
        hasFractionalPart = false
    }
}