package com.bonhams.expensemanagement.ui.batch

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
import com.bonhams.expensemanagement.adapters.NotificationListAdapter
import com.bonhams.expensemanagement.data.model.BatchData
import com.bonhams.expensemanagement.data.model.NotificationData
import com.bonhams.expensemanagement.data.model.NotificationItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.home.HomeFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject

private const val TAG = "NotificationFragment"

class BatchFragment() : Fragment(), RecylerCallback {
    private lateinit var viewModel: BatchViewModel

    companion object {
        fun newInstance() = BatchFragment()
    }

    private var contextActivity: BaseActivity? = null
    private var adapter: BatchListAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mNoResult: TextView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_batch, container, false)
        contextActivity = activity as? BaseActivity
        recyclerView = view.findViewById(R.id.recyclerView)
        mNoResult = view.findViewById(R.id.mNoResult)
        setupViewModel()
        getBatchDataObserver()
        return view
    }

    private fun setupRecyclerView(listOrders: List<BatchData>?) {
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
                        BatchListAdapter(
                            it,
                            it1,this
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
            BatchViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BatchViewModel::class.java)

    }
    private fun getBatchDataObserver() {
        val data= JsonObject()
        data.addProperty("numberOfItems",30)
        data.addProperty("page",1)
        viewModel.getBatchData(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${response.message}")
                                setupRecyclerView(response.batchData)

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

    override fun callback(action: String, data: Any, postion: Int) {
               println("action :$action")
        if(action == "details"){
            Constants.batch_allotted= (data as BatchData).batch_allotted.toString()
            val fragment = HomeFragment()
            (contextActivity as? MainActivity)?.changeFragment(fragment)
        }else if(action == "batch"){
            showBatchBottomSheet()
        }
    }
    private fun showBatchBottomSheet(){
        contextActivity?.let {
            val dialog = BottomSheetDialog(contextActivity!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.batch_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionPending = view.findViewById<TextView>(R.id.bottomOptionOne)
            val bottomOptionApproved = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val bottomOptionRejected = view.findViewById<TextView>(R.id.bottomOptionThree)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionPending.setOnClickListener {
                dialog.dismiss()
              //  viewModel.statusPicker.value = Constants.STATUS_PENDING
            }
            bottomOptionApproved.setOnClickListener {
                dialog.dismiss()
               // viewModel.statusPicker.value = Constants.STATUS_APPROVED
            }
            bottomOptionRejected.setOnClickListener {
                dialog.dismiss()
                //viewModel.statusPicker.value = Constants.STATUS_REJECTED
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}