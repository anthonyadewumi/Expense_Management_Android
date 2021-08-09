package com.bonhams.expensemanagement.ui.claims.claimDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity

private const val TAG = "ClaimDetailFragment"

class ClaimDetailFragment() : Fragment() {

    private var contextActivity: BaseActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_claim_detail, container, false)
        contextActivity = activity as? BaseActivity

        (contextActivity as MainActivity).setAppbarTitle("Claim Details")
        return view
    }
}