package com.bonhams.expensemanagement.ui.home.claims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ClaimsAndMileageAdapter
import com.bonhams.expensemanagement.ui.BaseActivity

private const val TAG = "NotificationFragment"

class ClaimsFragment() : Fragment() {
    companion object {
        fun newInstance() = ClaimsFragment()
    }

    private var contextActivity: BaseActivity? = null
    private var adapter: ClaimsAndMileageAdapter? = null
    private var mNotifyRecycler: RecyclerView? = null
    private var mNoResult: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        contextActivity = activity as? BaseActivity
        mNotifyRecycler = view.findViewById(R.id.mNotifyRecycler)
        mNoResult = view.findViewById(R.id.mNoResult)

        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        mNoResult?.visibility = View.GONE
        mNotifyRecycler?.visibility = View.VISIBLE
        if (adapter == null) {
            val linearLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            mNotifyRecycler?.layoutManager = linearLayoutManager
            adapter =
                ClaimsAndMileageAdapter(getDummyNotifications())
            mNotifyRecycler?.adapter = adapter
        } else {
            adapter?.listClaimsAndMileage = getDummyNotifications()
            adapter?.notifyDataSetChanged()
        }

    }

    private fun getDummyNotifications(): List<String> {
        return listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
    }
}