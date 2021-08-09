package com.bonhams.expensemanagement.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonhams.expensemanagement.ui.claims.ClaimsFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.MileageExpensesFragment
import com.bonhams.expensemanagement.ui.notification.NotificationFragment

class HomeViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val NUM_TABS = 2

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ClaimsFragment()
            1 -> return MileageExpensesFragment()
        }
        return NotificationFragment()
    }
}