package com.bonhams.expensemanagement.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.login.LoginActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.AppPreferences

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        checkUserToStartActivity()
    }

    private fun checkUserToStartActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            if (AppPreferences.isLoggedIn)
                startMainActivity()
            else
                startLoginActivity()

        }, 2000)
    }

    private fun startLoginActivity(){
        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    private fun startMainActivity(){
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish()
    }
}