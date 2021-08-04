package com.bonhams.expensemanagement.ui.forgotPassword

import android.os.Bundle
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity


class ForgotPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}