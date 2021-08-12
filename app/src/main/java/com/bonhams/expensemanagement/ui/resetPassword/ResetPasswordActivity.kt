package com.bonhams.expensemanagement.ui.resetPassword

import android.os.Bundle
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity


class ResetPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}