package com.bonhams.expensemanagement.ui.rmExpence

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ExpencesDetailsListAdapter
import com.bonhams.expensemanagement.adapters.ExpencesListAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.ActivityExpenceAcceptedBinding
import com.bonhams.expensemanagement.databinding.ActivityReportingMangerExpenceDetailsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class ReportingMangerExpenceDetails : BaseActivity(), RecylerCallback {
    private lateinit var binding: ActivityReportingMangerExpenceDetailsBinding
    private lateinit var viewModel: ExpenceViewModel
    public lateinit var toBeAcceptedDataList: List<ToBeAcceptedData>
    val idList = mutableListOf<Int>()
    val idListAll = mutableListOf<Int>()
    private var expenseCode: String = ""
    private var taxcodeId: String = ""
    private var requestid: String = "0"
    private var empname: String = ""
    private lateinit var expencesListAdapter: ExpencesDetailsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reporting_manger_expence_details)
        binding.lifecycleOwner = this
        val bundle: Bundle? = intent.extras
        requestid = intent.getStringExtra("employeeId").toString()
        empname = intent.getStringExtra("employeeName").toString()
        setupViewModel()
        setClickListeners()
        setDropdownDataObserver(" ")
        setupView()
        initSearch()
    }

    private fun setupView(){
        binding.chkAll.text = empname
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ExpenceViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ExpenceViewModel::class.java)
    }

    private fun initSearch() {
        binding.edtSearchClaim.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
                setDropdownDataObserver(binding.edtSearchClaim.text!!.trim().toString())
                true
            } else {
                false
            }
        }

    }

    private fun setClickListeners(){
      //  binding.appbarGreeting.text="rtio"
        binding.layoutAppBarSearch.setOnClickListener {
            if(binding.tilSearchClaim.isVisible){
                binding.tilSearchClaim.visibility=View.GONE
                binding.edtSearchClaim.setText("")
                setDropdownDataObserver(" ")

            }else{
                binding.tilSearchClaim.visibility=View.VISIBLE

            }
        }
        binding.layoutBack.setOnClickListener {
                finish()
        }
        binding.chkAll.setOnClickListener {
            if(binding.chkAll.isChecked)
            {
                idList.clear()
                idList.addAll(idListAll)
                showStatusFilterBottomSheet()

            }
        }
    }
    private fun setupExpenceRecyclerView(toBeAcceptedDataList: List<ExpenceDetailsData>){

        val linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rmExpenceDetails.layoutManager = linearLayoutManager
        expencesListAdapter = ExpencesDetailsListAdapter(toBeAcceptedDataList,this,this)
        binding.rmExpenceDetails.adapter = expencesListAdapter
    }
    private fun setDropdownDataObserver(serchkey:String) {
       val data= JsonObject()
        data.addProperty("requestId",requestid.toInt())
        viewModel.getRequestExpencesDetails(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                response.expenceDetailsData.forEach {
                                    idListAll.add(it.requestId)
                                }

                                setupExpenceRecyclerView(response.expenceDetailsData)

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun acceptRejectObserver(accept_reject:Int) {
        val data= JsonObject()
        val jsonIdArray=JsonArray()
        idList.forEach {jsonIdArray.add(it)}
        data.add("claim_ids",jsonIdArray)
        data.addProperty("action",accept_reject)
        data.addProperty("user_id",requestid.toInt())

        data.addProperty("role", AppPreferences.userType)
        viewModel.acceptReject(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()

                                setDropdownDataObserver("")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showStatusFilterBottomSheet(){
        this.let {
            val dialog = BottomSheetDialog(this, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.accept_reject_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionReject = view.findViewById<TextView>(R.id.bottomOptionReject)
            val bottomOptionAccept = view.findViewById<TextView>(R.id.bottomOptionAccept)
            bottomOptionAccept.setOnClickListener {
                dialog.dismiss()
                acceptRejectObserver(1)
            }
            bottomOptionReject.setOnClickListener {
                dialog.dismiss()
                acceptRejectObserver(2)

            }
            dialog.show()
        }
    }

    override fun callback(action: String, data: Any, postion: Int) {
        try {
            if(action == "checked"){
                idList.add((data as ExpenceDetailsData).requestId)
                showStatusFilterBottomSheet()
            }else{
                idList.remove((data as ExpenceDetailsData).requestId)

            }
        } catch (e: Exception) {
        }
        println("idList :$idList")

    }


}