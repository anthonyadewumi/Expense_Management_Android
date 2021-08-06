package com.bonhams.expensemanagement.ui.myProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity

private const val TAG = "NotificationFragment"

class MyProfileFragment() : Fragment() {

    companion object {
        fun newInstance() = MyProfileFragment()
    }

    private var contextActivity: BaseActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        contextActivity = activity as? BaseActivity

        return view
    }
}