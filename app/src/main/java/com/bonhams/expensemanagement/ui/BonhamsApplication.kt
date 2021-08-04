package com.bonhams.expensemanagement.ui

import android.app.Application
import com.bonhams.expensemanagement.utils.AppPreferences

class BonhamsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}