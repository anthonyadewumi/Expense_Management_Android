package com.bonhams.expensemanagement

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import com.bonhams.expensemanagement.utils.AppPreferences

class SplashScreenActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        checkUserToStartActivity()
    }

    private fun checkUserToStartActivity(){
        Handler(Looper.getMainLooper()).postDelayed({
            // Your Code
            if(AppPreferences.isLoggedIn)
                startMainActivity()
            else
                startLoginActivity()

        }, 3000)
    }

    private fun startLoginActivity(){
        val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun startMainActivity(){
        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}