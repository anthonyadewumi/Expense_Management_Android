package com.bonhams.expensemanagement.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
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
    }

    // This extension allow to call a function directly on the View to load an image.
    fun ImageView.loadImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .centerCrop()
            .into(this)
    }
}