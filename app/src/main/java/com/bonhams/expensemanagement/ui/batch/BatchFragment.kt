package com.bonhams.expensemanagement.ui.batch

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
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
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

private const val TAG = "NotificationFragment"

class BatchFragment() : Fragment(), RecylerCallback {
    private lateinit var viewModel: BatchViewModel

    companion object {
        fun newInstance() = BatchFragment()
    }

    private var contextActivity: BaseActivity? = null
    private var adapter: BatchListAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var spnCurrency: Spinner? = null
    private var txtTotalClaimed: TextView? = null
    private var mNoResult: TextView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_batch, container, false)
        contextActivity = activity as? BaseActivity
        recyclerView = view.findViewById(R.id.recyclerView)
        spnCurrency = view.findViewById(R.id.spnCurrency)
        txtTotalClaimed = view.findViewById(R.id.txt_total_claimed)
        mNoResult = view.findViewById(R.id.mNoResult)
        setupViewModel()
        getBatchDataObserver()
        getClaimTotal()
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


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            BatchViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(BatchViewModel::class.java)

    }

    private fun getClaimTotal() {
        val jsonObject = JsonObject().also {
            it.addProperty("data_of", "E")
        }
        viewModel.getClaimedTotal(jsonObject).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                viewModel.totalClaimedList=response.data
                                Log.d(TAG, "claim Total: ${resource.status}")
                                setupSpinners()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "claim TotalObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setupSpinners(){
        // Tax Adapter
        val currencyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.totalClaimedList
        )
        spnCurrency?.adapter = currencyAdapter
        spnCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                txtTotalClaimed?.text = String.format("%.2f",viewModel.totalClaimedList[position].total_amount.toString().toDouble())

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


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
        val batchNumber=(data as BatchData).batch_allotted
        if(action == "details"){
            Constants.batch_allotted= (data as BatchData).batch_allotted.toString()
            val fragment = HomeFragment()
            (contextActivity as? MainActivity)?.changeFragment(fragment)
        }else if(action == "batch"){
            showBatchBottomSheet(batchNumber)
        }
    }
    private fun showBatchBottomSheet(batch_number :Int){
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
                showDeleteBatchAlert(batch_number)
                dialog.dismiss()
               // viewModel.statusPicker.value = Constants.STATUS_APPROVED
            }
            bottomOptionRejected.setOnClickListener {
                submitBatchObserver(batch_number)
                dialog.dismiss()
                //viewModel.statusPicker.value = Constants.STATUS_REJECTED
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun showDeleteBatchAlert(batch_number :Int){
        contextActivity?.let { activity ->
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_alert_dialog)
            val title = dialog.findViewById(R.id.txtTitle) as TextView
            val body = dialog.findViewById(R.id.txtDescription) as TextView
            val input = dialog.findViewById(R.id.edtDescription) as EditText
            val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
            val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

            input.visibility = View.GONE
            title.text = resources.getString(R.string.delete_batch)
            body.text = resources.getString(R.string.are_you_sure_you_want_to_delete_batch)
            yesBtn.text = resources.getString(R.string.delete)
            noBtn.text = resources.getString(R.string.cancel)

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    deleteBatchObserver(batch_number)
                else
                    Toast.makeText(activity, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
    private fun deleteBatchObserver(batch_number :Int) {
        val data= JsonObject()
        data.addProperty("batch_number",batch_number)
        viewModel.deleteBatch(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "deleteBatchObserver: ${response.message}")
                                getBatchDataObserver()
                                Toast.makeText(contextActivity, response.message, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteBatchObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }
    private fun submitBatchObserver(batch_number :Int) {
        val data= JsonObject()
        data.addProperty("batch_number",batch_number)
        viewModel.submitBatch(data).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "submitBatchObserver: ${response.message}")
                                getBatchDataObserver()
                                Toast.makeText(contextActivity, response.message, Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "submitBatchObserver: ${it.message}")
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