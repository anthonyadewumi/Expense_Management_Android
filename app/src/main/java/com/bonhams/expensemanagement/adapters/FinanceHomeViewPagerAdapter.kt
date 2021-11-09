package com.bonhams.expensemanagement.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonhams.expensemanagement.ui.FinanceManger.FinanceManegerRequestFragment
import com.bonhams.expensemanagement.ui.claims.ClaimsFragment
import com.bonhams.expensemanagement.ui.home.FinanceHomeFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.MileageExpensesFragment

class FinanceHomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val NUM_TABS = 1

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return FinanceManegerRequestFragment()
        }
        return ClaimsFragment()
    }
}