package com.bonhams.expensemanagement.ui.myProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.myProfile.changePassword.ChangePasswordFragment

private const val TAG = "MyProfileFragment"

class MyProfileFragment() : Fragment() {

    private var contextActivity: BaseActivity? = null
    private var layoutChangePassword: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        contextActivity = activity as? BaseActivity
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword)
        setClickListeners()
        return view
    }


    private fun setClickListeners(){
        layoutChangePassword?.setOnClickListener(View.OnClickListener {
            val fragment = ChangePasswordFragment()
            changeFragment(fragment)
        })
    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager?.beginTransaction()?.replace(
            R.id.container,
            fragment,
            fragment.javaClass.simpleName
        )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
    }
}