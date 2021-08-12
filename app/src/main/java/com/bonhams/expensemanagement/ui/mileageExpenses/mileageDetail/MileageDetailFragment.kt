package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity

private const val TAG = "ClaimDetailFragment"

class MileageDetailFragment() : Fragment() {

    private var contextActivity: BaseActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_claim_detail, container, false)
        contextActivity = activity as? BaseActivity

        (contextActivity as MainActivity).setAppbarTitle(getString(R.string.mileage_details))
        (contextActivity as MainActivity).showAppbarBackButton(true)

        return view
    }
}