package com.bonhams.expensemanagement.ui.claims.newClaim

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.databinding.ActivityLoginBinding
import com.bonhams.expensemanagement.databinding.ActivitySplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.splitItmlist
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.totalAmount
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
    private var compnyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_split_claim)
        binding.lifecycleOwner = this
        setupViewModel()
        setDropdownDataObserver()
        setClickListeners()

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }
    private fun setClickListeners(){
        binding.btnSubmit.setOnClickListener {
            if(validatesplitClaim()) {

                try {
                    val companyOne = binding.spnCompany.selectedItem as Company?
                    val departmentOne = binding.spnDepartment.selectedItem as Department?
                    val expenseTypeOne = binding.spnExpense.selectedItem as ExpenseType?
                    val totalAmountOne = binding.edtTotalAmount.getNumericValue()
                    val taxCodeOne = binding.edtTaxCode.text
                    val mtax = binding.edtTax.getNumericValue()
                    println("companynumber :" + companyOne?.id)
                    println("companycode :" + companyOne?.code)
                    println("departmentOne :" + departmentOne?.id)
                    println("expenseTypeOne :" + expenseTypeOne?.id)
                    println("taxcode id :" + expenseTypeOne?.taxCodeID)
                    println("expence codeid id :" + expenseTypeOne?.expenseCodeID)
                    val splitOne = SplitClaimItem(
                        companyOne?.id?:"0",
                        companyOne?.code?:"0", departmentOne?.id?:"0", expenseTypeOne?.id!!,
                        totalAmountOne.toString(),
                        expenseTypeOne.taxCodeID, mtax,companyOne?.name?:"",departmentOne?.name?:"",
                        expenseTypeOne.name,binding.edtAutionValue.text.toString(),binding.tvAuctionExpCode.text.toString(),
                        expenseTypeOne.expenseCodeID
                    )

                    splitItmlist.add(splitOne)
                    finish()

                } catch (e: Exception) {
                }
            }
        }
        binding.layoutBack.setOnClickListener {
                finish()
        }
        binding.edtAutionValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!binding.edtAutionValue.text.toString().isEmpty()){
                    binding.tvAuctionExpCode.text = expenseCode

                }else{
                    binding.tvAuctionExpCode.text = ""
                }
            }
        })
    }
    private fun validatesplitClaim(): Boolean {

        if(binding.edtTotalAmount.text.isNullOrEmpty()){
            Toast.makeText(this, "Please Enter The Amount", Toast.LENGTH_SHORT).show()
            return false
        }else if(binding.edtTax.text.isNullOrEmpty()){
            Toast.makeText(this, "Please Enter Applicable Tax ", Toast.LENGTH_SHORT).show()
           return false
        }
        val amount=binding.edtTotalAmount.getNumericValue()
        return if(amount<totalAmount){
            totalAmount -= amount
            true
        }else {
            Toast.makeText(this, "Please Enter The Amount below $totalAmount", Toast.LENGTH_SHORT)
                .show()
            false

        }
       return true
    }
    private fun setDropdownDataObserver() {

        val currancyCode = intent.getSerializableExtra("currencyCode") as String?
        val currencySymbol = intent.getSerializableExtra("currencySymbol") as String?
        if (currencySymbol != null) {
            binding.edtTotalAmount.setCurrencySymbol(currencySymbol, useCurrencySymbolAsHint = true)
            binding.edtTax.setCurrencySymbol(currencySymbol, useCurrencySymbolAsHint = true)
        }
        if (currancyCode != null) {
            binding.edtTotalAmount.setLocale(currancyCode)
            binding.edtTax.setLocale(currancyCode)
        }

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
        viewModel.expenseTypeListExpenseGroup = dropdownResponse.expenseType.toMutableList()
        viewModel.departmentListCompany = dropdownResponse.departmentList as MutableList<Department>
        viewModel.currencyList  = dropdownResponse.currencyType as MutableList<Currency>
        viewModel.carTypeList  = dropdownResponse.carType
        viewModel.statusTypeList  = dropdownResponse.statusType
        viewModel.mileageTypeList  = dropdownResponse.mileageType
        viewModel.companyList  = dropdownResponse.companyList
        viewModel.taxList  = dropdownResponse.tax

        setupSpinners()
    }
    private fun setupSpinners(){


        // Expense Group Adapter
        val expenseGroupAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.expenseGroupList
        )
        binding.spnExpenseGroup.adapter = expenseGroupAdapter
        binding.spnExpenseGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val groupid =viewModel.expenseGroupList[position].id
                println("selected group ID :$groupid")
                viewModel.expenseTypeList.clear()
                viewModel.expenseTypeListExpenseGroup.forEach {
                    if(it.expenseGroupID == groupid&&it.companyID==compnyId.toString()){
                        viewModel.expenseTypeList.add(it)
                        // println("selected expenseTypeList Added :" )

                    }else if(it.companyID.isNullOrEmpty()){
                        viewModel.expenseTypeList.add(it)

                    }
                }
                setupExpenceType()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


       /* // Expense Type Adapter
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
*/
        // Company List Adapter
        val companyAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.companyList
        )
        binding.spnCompany.adapter = companyAdapter
      /*  viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.company.equals(element.name)){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompany.setSelection(compnypostion)*/

        binding.spnCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                compnyId=viewModel.companyList[position].id.toInt()
                viewModel.departmentList.clear()
                viewModel.departmentListCompany.forEach {
                    println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                    if(it.company_id == compnyId.toString()){
                        viewModel.departmentList.add(it)
                    }
                }
                setupDeparmentType()

                // System.out.println("selected appoint :"+ viewModel.companyList[position].code)
                //binding.edtTitle.setText(viewModel.companyList[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }




    }
    private fun setupExpenceType(){
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
                taxcodeId= viewModel.expenseTypeList[position].taxCodeID
                viewModel.taxList.forEach {

                    if(it.id.toString() == taxcodeId) {
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
    }
    private fun setupDeparmentType() {
        // Department Adapter
        val departmentAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
        /* var postion=0
        viewModel.departmentList.forEachIndexed { index, element ->

            if(AppPreferences.department.equals(element.name)){
                postion=index
                return@forEachIndexed
            }
        }
        binding.spnDepartment.setSelection(postion)*/
        binding.spnDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }
    }