package com.bonhams.expensemanagement.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.NotificationAdapter
import com.bonhams.expensemanagement.data.model.NotificationItem
import com.bonhams.expensemanagement.ui.BaseActivity

private const val TAG = "NotificationFragment"

class NotificationFragment() : Fragment() {

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private var contextActivity: BaseActivity? = null
    private var adapter: NotificationAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mNoResult: TextView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        contextActivity = activity as? BaseActivity
        recyclerView = view.findViewById(R.id.recyclerView)
        mNoResult = view.findViewById(R.id.mNoResult)

        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        mNoResult?.visibility = View.GONE
        recyclerView?.visibility = View.VISIBLE
        if (adapter == null) {
            val linearLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView?.layoutManager = linearLayoutManager
            adapter =
                NotificationAdapter(getDummyNotifications()/*viewModelActivity.responseNotification?.value?.response?.dataNotificationList*/)
            recyclerView?.adapter = adapter
        } else {
            adapter?.listOrders =
                getDummyNotifications()//viewModelActivity.responseNotification?.value?.response?.dataNotificationList
            adapter?.notifyDataSetChanged()
        }
    }

    private fun getDummyNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(), NotificationItem(),
            NotificationItem(), NotificationItem(), NotificationItem()
        )
    }

}