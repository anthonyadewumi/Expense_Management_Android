package com.bonhams.expensemanagement.ui.login

import android.content.Intent
import android.os.Bundle
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mContinue.setOnClickListener {
            login()
        }
        mRememberMeCheckbox.setOnClickListener {
            setRememberMe()
        }
        mForgotPassword.setOnClickListener {
            onForgotPasswordClick()
        }
    }

    fun setRememberMe() {
        if (mRememberMeCheckbox.tag == 0) {
            mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_checked_login)
            mRememberMeCheckbox.setTag(1)
        } else {
            mRememberMeCheckbox.setImageResource(R.drawable.ic_checkbox_uncheck_login)
            mRememberMeCheckbox.setTag(0)
        }
    }

    private fun onForgotPasswordClick() {
        val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    private fun login() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

}