package com.bonhams.expensemanagement.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.HomeViewPagerAdapter
import com.bonhams.expensemanagement.ui.BaseActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {


    private var contextActivity: BaseActivity? = null
    private var adapter: HomeViewPagerAdapter? = null
    private var mNotifyRecycler: RecyclerView? = null
    private var mNoResult: TextView? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    private val tabsArray = arrayOf(
        "Claims",
        "Mileage Expenses",
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        contextActivity = activity as? BaseActivity
        mNotifyRecycler = view.findViewById(R.id.mNotifyRecycler)
        mNoResult = view.findViewById(R.id.mNoResult)
        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.viewPager)
        setupViewPager()
        return view
    }

    private fun setupViewPager() {
        adapter = HomeViewPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()
    }
}