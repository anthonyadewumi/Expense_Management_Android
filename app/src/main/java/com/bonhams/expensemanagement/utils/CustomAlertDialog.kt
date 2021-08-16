package com.bonhams.expensemanagement.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.bonhams.expensemanagement.R

class CustomAlertDialog (context: Context) : Dialog(context) {

    private lateinit var txtTitle: TextView
    private lateinit var txtDescription: TextView
    private lateinit var edtDescription: EditText
    private lateinit var btnPositive: Button
    private lateinit var btnNegative: Button
    private var callback: DialogButtonCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog, null)

        txtTitle = view.findViewById(R.id.txtTitle)
        txtDescription = view.findViewById(R.id.txtDescription)
        edtDescription = view.findViewById(R.id.edtDescription)
        btnPositive = view.findViewById(R.id.btnPositive)
        btnNegative = view.findViewById(R.id.btnNegative)

        setClickListeners()
        setContentView(view)
    }

    private fun setClickListeners(){
        btnPositive.setOnClickListener(View.OnClickListener {
            callback?.onPositiveClick()
        })
        btnNegative.setOnClickListener(View.OnClickListener {
            callback?.onNegativeClick()
        })
    }

    fun setAlertCancelable(cancelable: Boolean){
        setCancelable(cancelable)
    }

    fun showPositiveButton(show: Boolean) {
        if (show) {
            btnPositive.visibility = View.VISIBLE
            btnNegative.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        } else {
            btnPositive.visibility = View.GONE
            btnNegative.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 2f
            )
        }
    }

    fun showNegativeButton(show: Boolean) {
        if (show) {
            btnNegative.visibility = View.VISIBLE
            btnPositive.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        } else {
            btnNegative.visibility = View.GONE
            btnPositive.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 2f
            )
        }
    }

    fun showEditText(show: Boolean) {
        if (show) edtDescription.visibility = View.VISIBLE else edtDescription.visibility =
            View.GONE
    }

    fun showDescriptionText(show: Boolean) {
        if (show) txtDescription.visibility = View.VISIBLE else txtDescription.visibility =
            View.GONE
    }

    fun setEditTextDescription(description: String) {
        edtDescription.setText(description)
        edtDescription.requestFocus()
        edtDescription.setSelection(description.trim { it <= ' ' }.length)
    }

    fun getEditTextDescription(): String {
        return edtDescription.text.toString().trim { it <= ' ' }
    }

    fun setPositiveText(text: CharSequence?) {
        btnPositive.text = text
    }

    fun setNegativeText(text: CharSequence?) {
        btnNegative.text = text
    }


    /*
     * Call back interface
     * */
    fun getCallback(): DialogButtonCallback? {
        return callback
    }

    fun setCallback(dialogCallback: DialogButtonCallback?) {
        callback = dialogCallback
    }

    interface DialogButtonCallback {
        fun onPositiveClick()
        fun onNegativeClick()
    }
}
