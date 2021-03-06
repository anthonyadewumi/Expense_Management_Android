package com.bonhams.expensemanagement.utils

import android.content.Context
import android.location.Location
import android.preference.PreferenceManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.bonhams.expensemanagement.R
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {
        const val KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates"

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

        fun getFormattedDate2(dateStr: String, inputFormat: String): String {
            val inputFormatter = SimpleDateFormat(inputFormat)
            if (dateStr.trim().isNotEmpty()) {
                val date = inputFormatter.parse(dateStr)
                date?.let {
                    var dateFomate = Constants.YYYY_MM_DD_SERVER_REQUEST_FORMAT
                    val formatter = SimpleDateFormat(dateFomate)
                    return formatter.format(date)
                }
            }
            return dateStr
        }

        fun getFormattedDate(dateStr: String, inputFormat: String, countryFormat: String): String {
            val inputFormatter = SimpleDateFormat(inputFormat)
            if (dateStr.trim().isNotEmpty()) {
                val date = inputFormatter.parse(dateStr)
                date?.let {
                    var dateFomate = ""
                    dateFomate = if (countryFormat == "USA") {
                        Constants.DD_MMM_YYYY_FORMAT
                    } else {
                        Constants.MMM_DD_YYYY_FORMAT

                    }
                    val formatter = SimpleDateFormat(dateFomate)
                    return formatter.format(date)
                }
            }
            return dateStr
        }

        fun getDateInServerRequestFormat(dateStr: String, inputFormat: String): String {
            val inputFormatter = SimpleDateFormat(inputFormat)
            if (dateStr.trim().isNotEmpty()) {
                val date = inputFormatter.parse(dateStr)
                date?.let {
                    val formatter = SimpleDateFormat(Constants.YYYY_MM_DD_SERVER_REQUEST_FORMAT)
                    return formatter.format(date)
                }
            }
            return dateStr
        }

        fun getDateInDisplayFormat(epoch: Long): String {
            val date = Date(epoch)
            val formatter = SimpleDateFormat(Constants.DD_MMM_YYYY_FORMAT)
            return formatter.format(date)
        }

        fun getDateInDisplayFormatWithCountry(epoch: Long, countryFormat: String): String {
            val date = Date(epoch)
            var dateFomate = ""
            dateFomate = if (countryFormat == "USA") {
                Constants.MMM_DD_YYYY_FORMAT
            } else {
                Constants.DD_MM_YYYY_FORMAT

            }
            val formatter = SimpleDateFormat(dateFomate)
            return formatter.format(date)
        }

        fun getDateInDisplayFormatWithCountry2(epoch: Long): String {
            val date = Date(epoch)
            var dateFomate = Constants.YYYY_MM_DD_SERVER_REQUEST_FORMAT
            val formatter = SimpleDateFormat(dateFomate)
            return formatter.format(date)
        }

        fun getDateInServerRequestFormat(epoch: Long): String {
            val date = Date(epoch)
            val formatter = SimpleDateFormat(Constants.YYYY_MM_DD_SERVER_REQUEST_FORMAT)
            return formatter.format(date)
        }

        fun String.capitalizeFirstLetter(): String {
            return if (!this.isNullOrEmpty() && this.trim().isNotEmpty()) {
                this.replaceFirstChar(Char::uppercase)
            } else {
                ""
            }
        }

        // This extension allow to call a function directly on the View to load an image.
        fun ImageView.loadImage(imageUrl: String) {
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(this)
        }

        fun EditText.showKeyboard(context: Context?, show: Boolean) {
            val imm: InputMethodManager? = context?.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager?

            if (show) {
                this.isFocusableInTouchMode = true
                this.isFocusable = true
                this.isClickable = true
                this.isEnabled = true
                this.isCursorVisible = true
                this.requestFocus();
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            } else {
                imm?.hideSoftInputFromWindow(this.windowToken, 0)
                this.clearFocus();
                /*this.isFocusableInTouchMode = false
                this.isFocusable = false
                this.isClickable = false
                this.isEnabled = false
                this.isCursorVisible = false*/
            }
        }


        fun requestingLocationUpdates(context: Context?): Boolean {
            return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(
                    KEY_REQUESTING_LOCATION_UPDATES,
                    false
                )
        }

        /**
         * Stores the location updates state in SharedPreferences.
         * @param requestingLocationUpdates The location updates state.
         */
        fun setRequestingLocationUpdates(context: Context?, requestingLocationUpdates: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(
                    KEY_REQUESTING_LOCATION_UPDATES,
                    requestingLocationUpdates
                )
                .apply()
        }

        /**
         * Returns the `location` object as a human readable string.
         * @param location  The [Location].
         */
        fun getLocationText(location: Location?): String? {
            return if (location == null) "Unknown location" else "(" + location.latitude + ", " + location.longitude + ")"
        }

        fun getLocationTitle(context: Context): String? {
            return context.getString(
                R.string.location_updated,
                DateFormat.getDateTimeInstance().format(Date())
            )
        }
    }
}