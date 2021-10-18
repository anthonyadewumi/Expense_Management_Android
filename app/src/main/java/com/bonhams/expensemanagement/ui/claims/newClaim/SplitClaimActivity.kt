package com.bonhams.expensemanagement.ui.claims.newClaim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.databinding.ActivityLoginBinding
import com.bonhams.expensemanagement.databinding.ActivitySplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.splitItmlist
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class SplitClaimActivity : BaseActivity() {
    private lateinit var binding: ActivitySplitClaimBinding
    private lateinit var viewModel: NewClaimViewModel
    private var expenseCode: String = ""
    private var taxcodeId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_split_claim)
        binding.lifecycleOwner = this
        setupViewModel()
        setClickListeners()
        setDropdownDataObserver()
        setupView()
    }

    private fun setupView(){
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }
    private fun setClickListeners(){
        binding.btnSubmit.setOnClickListener {
            val companyOne = binding.spnCompany.selectedItem as Company?
            val departmentOne = binding.spnDepartment.selectedItem as Department?
            val expenseTypeOne = binding.spnExpense.selectedItem as ExpenseType?
            val totalAmountOne = binding.edtTotalAmount.text
            val taxCodeOne = binding.edtTaxCode.text
            val tax = binding.edtTax.text
            val splitOne = SplitClaimItem(companyOne?.id!!,companyOne?.code!!, departmentOne?.id!!, expenseTypeOne?.expenseCodeID!!,
                totalAmountOne.toString(), taxCodeOne.toString(),tax.toString())
                splitItmlist.add(splitOne)
                finish()

        }
        binding.layoutBack.setOnClickListener {
                finish()
        }

    }
    private fun setDropdownDataObserver() {
        viewModel.getDropDownData().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                initializeSpinnerData(response)
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

    private fun initializeSpinnerData(dropdownResponse: DropdownResponse){
        viewModel.expenseGroupList = dropdownResponse.expenseGroup
        viewModel.expenseTypeList = dropdownResponse.expenseType
        viewModel.departmentList = dropdownResponse.departmentList
        viewModel.currencyList  = dropdownResponse.currencyType
        viewModel.carTypeList  = dropdownResponse.carType
        viewModel.statusTypeList  = dropdownResponse.statusType
        viewModel.mileageTypeList  = dropdownResponse.mileageType
        viewModel.companyList  = dropdownResponse.companyList
        viewModel.taxList  = dropdownResponse.tax

        setupSpinners()
    }
    private fun setupSpinners(){


        // Expense Type Adapter
        val expenseTypeAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.expenseTypeList
        )
        binding.spnExpense.adapter = expenseTypeAdapter
        binding.spnExpense.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                expenseCode=viewModel.expenseTypeList.get(position).activityCode
                taxcodeId=viewModel.expenseTypeList.get(position).taxCodeID
                viewModel.taxList.forEach {
                    if(it.id.toString().equals(taxcodeId)) {
                        binding.edtTaxCode.setText(it.tax_code)

                    }
                }

                if(!binding.edtAutionValue.text.toString().isEmpty()){
                    binding.tvAuctionExpCode.text = expenseCode

                }else{
                    binding.tvAuctionExpCode.text = ""
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Company List Adapter
        val companyAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.companyList
        )
        binding.spnCompany.adapter = companyAdapter
        var compnypostion=0
        viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.company.equals(element.name)){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompany.setSelection(compnypostion)

        binding.spnCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // System.out.println("selected appoint :"+ viewModel.companyList[position].code)
                //binding.edtTitle.setText(viewModel.companyList[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        val departmentAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
        var postion=0
        viewModel.departmentList.forEachIndexed { index, element ->

            if(AppPreferences.department.equals(element.name)){
                postion=index
                return@forEachIndexed
            }
        }
        binding.spnDepartment.setSelection(postion)
        binding.spnDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }



    }


}