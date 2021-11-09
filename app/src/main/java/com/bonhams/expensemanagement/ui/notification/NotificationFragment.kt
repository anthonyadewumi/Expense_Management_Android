package com.bonhams.expensemanagement.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.NotificationAdapter
import com.bonhams.expensemanagement.adapters.NotificationListAdapter
import com.bonhams.expensemanagement.data.model.NotificationData
import com.bonhams.expensemanagement.data.model.NotificationItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailViewModel
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailViewModelFactory
import com.bonhams.expensemanagement.utils.Status
import com.google.gson.JsonObject

private const val TAG = "NotificationFragment"

class NotificationFragment() : Fragment() {
    private lateinit var viewModel: NotificationViewModel

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private var contextActivity: BaseActivity? = null
    private var adapter: NotificationListAdapter? = null
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
        setupViewModel()
        getNotificationDataObserver()
        return view
    }

    private fun setupRecyclerView(listOrders: List<NotificationData>?) {
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
                listOrders?.let {
                    context?.let { it1 ->
                        NotificationListAdapter(
                            it,
                            it1/*viewModelActivity.responseNotification?.value?.response?.dataNotificationList*/
                        )
                    }
                }
            recyclerView?.adapter = adapter
        } else {
            if (listOrders != null) {
                adapter?.listOrders =
                    listOrders
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun getDummyNotifications(): List<NotificationItem> {
        return listOf(
            NotificationItem(), NotificationItem(),
            NotificationItem(), NotificationItem(), NotificationItem()
        )
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NotificationViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NotificationViewModel::class.java)

    }
    private fun getNotificationDataObserver() {
        val data= JsonObject()
        data.addProperty("numberOfItems",20)
        data.addProperty("page",1)
        viewModel.getnotificationdata(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${response.message}")
                                setupRecyclerView(response.notificationData)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
}