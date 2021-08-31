package com.bonhams.expensemanagement.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object{

        fun changeDateFormat(time: String, inputPattern: String?, outputPattern: String?): String? {
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)
            var str: String? = time
            if (time.trim().isNotEmpty()) {
                try {
                    val date = inputFormat.parse(time)
                    str = outputFormat.format(date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            return str
        }

        fun getFormattedDate(dateStr: String, inputFormat: String): String {
            val inputFormatter = SimpleDateFormat(inputFormat)
            val date = inputFormatter.parse(dateStr)
            date?.let {
                val formatter = SimpleDateFormat(Constants.DD_MMM_YYYY_FORMAT)
                return formatter.format(date)
            }
            return dateStr
        }

        fun getDateInDisplayFormat(epoch: Long): String {
            val date = Date(epoch)
            val formatter = SimpleDateFormat(Constants.DD_MMM_YYYY_FORMAT)
            return formatter.format(date)
        }

        fun getDateInServerRequestFormat(epoch: Long): String {
            val date = Date(epoch)
            val formatter = SimpleDateFormat(Constants.YYYY_MM_DD_SERVER_REQUEST_FORMAT)
            return formatter.format(date)
        }

        // This extension allow to call a function directly on the View to load an image.
        fun ImageView.loadImage(imageUrl: String) {
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(this)
        }

        fun EditText.showKeyboard(context: Context?, show: Boolean){
            val imm: InputMethodManager? = context?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?

            if(show) {
                this.isFocusableInTouchMode = true
                this.isFocusable = true
                this.isClickable = true
                this.isEnabled = true
                this.isCursorVisible = true
                this.requestFocus();
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
            else{
                imm?.hideSoftInputFromWindow(this.windowToken, 0)
                this.clearFocus();
                /*this.isFocusableInTouchMode = false
                this.isFocusable = false
                this.isClickable = false
                this.isEnabled = false
                this.isCursorVisible = false*/
            }
        }
    }
}