package com.bonhams.expensemanagement.ui.main

import android.os.Bundle
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}