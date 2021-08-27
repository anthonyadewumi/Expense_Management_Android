package com.bonhams.expensemanagement.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object{

        fun getFormattedDate(dateStr: String, inputFormat: String): String {
            val inputFormatter = SimpleDateFormat(inputFormat)
            val date = inputFormatter.parse(dateStr)
            date?.let {
                val formatter = SimpleDateFormat(Constants.DD_MM_YYYY_FORMAT)
                return formatter.format(date)
            }
            return ""
        }

        fun getDateInDisplayFormat(epoch: Long): String {
            val date = Date(epoch)
            val formatter = SimpleDateFormat(Constants.DD_MMM_YYYY_FORMAT)
            return formatter.format(date)
        }

        // This extension allow to call a function directly on the View to load an image.
        fun ImageView.loadImage(imageUrl: String) {
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(this)
        }

        fun TextInputEditText.showKeyboard(context: Context, show: Boolean){
            val imm: InputMethodManager? = context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?

            if(show) {
                this.isFocusableInTouchMode = true
                this.isFocusable = true
                this.requestFocus();
                this.isCursorVisible = true
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
            else{
                imm?.hideSoftInputFromWindow(this.windowToken, 0)
                this.isFocusableInTouchMode = false
                this.clearFocus();
                this.isFocusable = false
                this.isCursorVisible = false
            }
        }
    }
}