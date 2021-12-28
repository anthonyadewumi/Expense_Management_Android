package com.bonhams.expensemanagement.utils

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*



private fun Context.createAppSettingsIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}

fun Activity.isServiceRunning(serviceClassName: String): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Integer.MAX_VALUE)
        .any { it.service.className == serviceClassName }
}

fun String?.toDate(): Date {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = simpleDateFormat.parse(this)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    outputFormat.timeZone = TimeZone.getDefault();
    return outputFormat.parse(outputFormat.format(date))
    // return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(this)
}

fun String?.toDateServer(): Date {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    return simpleDateFormat.parse(this)
    // return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(this)
}

fun Double.toMiles(): Double {
    return this / 1609.3440057765;
}

fun String?.formatDate(): String {
    return try {
        if (this.isNullOrEmpty() || this == "null") "N/A"
        else SimpleDateFormat(
            "dd MMM yyy, hh:mm a",
            Locale.getDefault()
        ).format(
            SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
            ).parse(this)!!
        )
    } catch (ex: java.lang.Exception) {
        "N/A"
    }

}

fun Long?.formatDate(): String {
    return try {
        if (this == null || this == 0L) "N/A"
        else SimpleDateFormat(
            "MMM ddm, yyyy",
            Locale.getDefault()
        ).format(Date(this))
    } catch (ex: java.lang.Exception) {
        "N/A"
    }

}

fun String?.validateCurrency(): String {
    return if (this.isNullOrEmpty()) "N/A"
    else if (!this.isDigitsOnly()) "N/A"
    else "$".plus(
        NumberFormat.getNumberInstance(Locale.US).format(this.toInt())
    )
}

fun String?.formatDateNew(): String {
    return try {
        if (this.isNullOrEmpty() || this == "null") "N/A"
        else {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = simpleDateFormat.parse(this)
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
            outputFormat.timeZone = TimeZone.getDefault();
            outputFormat.format(date)
        }
    } catch (ex: java.lang.Exception) {
        "N/A"
    }


}

fun String?.toFormattedDate(): String {
    return if (this == null) "N/A"
    else if (this == "N/A" || this == "NA" || !this.isDigitsOnly()) {
        return "N/A"
    } else {
        val netDate = Date(this.toLong() * 1000)
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(netDate)
    }

}

fun Date.formatDate(): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(this)
}

fun String?.toDateNew(): Date {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val date = simpleDateFormat.parse(this)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    outputFormat.timeZone = TimeZone.getDefault();
    return outputFormat.parse(outputFormat.format(date))
    // return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(this)
}


fun String?.isValidEmail() =
    this != null && android.util.Patterns.EMAIL_ADDRESS.matcher(this.trim()).matches()


fun String.getDateWithServerTimeStamp(): Date? {
    val dateFormat = SimpleDateFormat(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        Locale.getDefault()
    )
    dateFormat.timeZone = TimeZone.getDefault()  // IMP !!!
    return try {
        dateFormat.parse(this)
    } catch (e: Exception) {
        null
    }
}


fun Date.getStringTimeStampWithDate(): String? {
    val dateFormat = SimpleDateFormat(
        "dd MMM yyy hh:mm:ss a",
        Locale.getDefault()
    )
    dateFormat.timeZone = TimeZone.getTimeZone("GMT")
    try {
        return dateFormat.format(this)
    } catch (e: Exception) {
        return null
    }

}

fun Double.roundTo(n: Int): Double {
    return "%.${n}f".format(this).toDouble()
}

fun String?.formatNullEmpty(): String {
    return if ((this.isNullOrEmpty()) || this.toLowerCase(Locale.ROOT) == "null") return "N/A" else this

}

fun String?.formatName(): String {
    return if ((this.isNullOrEmpty()) || this.toLowerCase(Locale.ROOT) == "null") return "No Name" else this

}

fun String?.formatAddress(): String {
    return if ((this.isNullOrEmpty()) || this.toLowerCase(Locale.ROOT) == "null") return "No Address" else this

}


fun Int?.formatNull(): Int {
    return this ?: return 0
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun String.isValidMobile() = Patterns.PHONE.matcher(this).matches()

fun PopupWindow.dimBehind() {
    val container = contentView.rootView
    val context = contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val p = container.layoutParams as WindowManager.LayoutParams
    p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    p.dimAmount = 0.3f
    wm.updateViewLayout(container, p)
}
fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val str = this@addDecimalLimiter.text!!.toString()
            if (str.isEmpty()) return
            val str2 = decimalLimiter(str, maxLimit)

            if (str2 != str) {
                this@addDecimalLimiter.setText(str2)
                val pos = this@addDecimalLimiter.text!!.length
                this@addDecimalLimiter.setSelection(pos)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}
fun EditText.decimalLimiter(string: String, MAX_DECIMAL: Int): String {

    var str = string
    if (str[0] == '.') str = "0$str"
    val max = str.length

    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char

    val decimalCount = str.count{ ".".contains(it) }

    if (decimalCount > 1)
        return str.dropLast(1)

    while (i < max) {
        t = str[i]
        if (t != '.' && !after) {
            up++
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > MAX_DECIMAL)
                return rFinal
        }
        rFinal += t
        i++
    }
    return rFinal
}
fun getMimeType(url: String?): String? {
    var type: String? = null
    val extension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (extension != null) {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
    return type
}

@Throws(IOException::class)
fun getBytes(`is`: InputStream): ByteArray {
    val byteBuff = ByteArrayOutputStream()
    val buffSize = 1024
    val buff = ByteArray(buffSize)
    var len = 0
    while (`is`.read(buff).also { len = it } != -1) {
        byteBuff.write(buff, 0, len)
    }
    return byteBuff.toByteArray()
}




fun EditText.onlyNumbers() {
    inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
            InputType.TYPE_NUMBER_FLAG_SIGNED
    keyListener = DigitsKeyListener.getInstance("0123456789")
}

fun Context.openUrl(url: String) {
    val web = Uri.parse(url)
    val i = Intent(Intent.ACTION_VIEW, web)
    ContextCompat.startActivity(this, Intent.createChooser(i, "Choose Application"), null)
}



