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
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragmentEdit.Companion.remaningAmount
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragmentEdit.Companion.splitItmlist
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

class SplitClaimActivity2 : BaseActivity() {
    private lateinit var binding: ActivitySplitClaimBinding
    private lateinit var viewModel: NewClaimViewModel
    private var expenseCode: String = ""
    private var mtaxcodeId: String = ""
    private var taxcodeId: String = ""
    private var taxcodeValue: String = ""
    private var compnyId: Int = 0
    private var groupId: String = ""
    private var expenceType: String = ""
    private var companyNumber: String = ""
    private var department: String = ""
    public var taxAmount: Double=0.00
    public var totalAmountForsplit: Double=0.00
    private var groupName: String = ""

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
        binding.edtGroupValue.setOnClickListener {

            binding.spnExpenseGroup.performClick()

        }
        binding.edtExpenceTypeValue.setOnClickListener {

            binding.spnExpense.performClick()

        }

        binding.btnSubmit.setOnClickListener {
            if(validatesplitClaim()) {

                try {
                    val companyOne = binding.spnCompany.selectedItem as Company?
                    val departmentOne = binding.spnDepartment.selectedItem as Department?
                    val expenseTypeOne = binding.spnExpense.selectedItem as ExpenseType?
                    val totalAmountOne = binding.edtTotalAmount.text
                    val taxCodeOne = binding.edtTaxCode.text
                    val mtax = binding.edtTax.text.toString().toDouble()
                    val splitOne = SplitClaimItem(
                        companyOne?.id?:"0",
                        companyOne?.code?:"0", departmentOne?.id?:"0", expenseTypeOne?.id!!,
                        totalAmountOne.toString(),
                        expenseTypeOne.taxCodeID, mtax,companyOne?.name?:"",departmentOne?.name?:"",
                        expenseTypeOne.name,binding.edtAutionValue.text.toString(),binding.tvAuctionExpCode.text.toString(),
                        expenseTypeOne.expenseCodeID,taxcodeValue
                    )
                    println("item :$splitOne")

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
    private fun setupTax(){
        when (AppPreferences.userType) {
            "Reporting Manager" -> { }
            "Finance Department" -> { }
            "Admin" -> { }
            "Final Approver" -> { }
            else->{
                viewModel.taxList.forEach {
                    if(it.id.toString() == mtaxcodeId){
                        viewModel.taxList= listOf(it)
                        return@forEach
                    }
                }
            }
        }
        val taxAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.taxList
        )
        binding.spntaxcode.adapter = taxAdapter

        var postion=0
        viewModel.taxList.forEachIndexed { index, element ->

            if(element.id.toString() == mtaxcodeId) {
                postion=index
                return@forEachIndexed
                //  binding.edtTaxcode.setText(it.tax_code)

            }
        }
        binding.spntaxcode.setSelection(postion)

        binding.spntaxcode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                taxcodeId= viewModel.taxList[position].id.toString()
                taxcodeValue= viewModel.taxList[position].tax_code

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }


    private fun validatesplitClaim(): Boolean {

        if(binding.edtTotalAmount.text.isNullOrEmpty()){
            Toast.makeText(this, "Please Enter The Amount", Toast.LENGTH_SHORT).show()
            return false
        }else if(binding.edtTax.text.isNullOrEmpty()){
            Toast.makeText(this, "Please Enter Applicable Tax ", Toast.LENGTH_SHORT).show()
           return false
        }
             val mtotal=binding.edtTotalAmount.text.toString().toDouble()
             val mtax=binding.edtTax.text.toString().toDouble()
        if(mtax>mtotal){
            Toast.makeText(this, "The tax amount must not be greater than the total amount", Toast.LENGTH_SHORT).show()
            return false
            binding.edtTax.setText("")

        }

        val amount=String.format("%.2f",binding.edtTotalAmount.text.toString().toDouble()).toDouble()
        val mremaningAmount=String.format("%.2f",remaningAmount).toDouble()
        return if(amount<= totalAmountForsplit){
            true
        }else {
            Toast.makeText(this, "Please Enter The Amount below ${mremaningAmount}", Toast.LENGTH_SHORT)
                .show()
            false

        }
       return true
    }
    private fun setDropdownDataObserver() {
        when (AppPreferences.userType) {
            "Reporting Manager" -> { }
            "Finance Department" -> { }
            "Admin" -> { }
            "Final Approver" -> { }
            else->{
                binding.edtTax.isEnabled=false
            }
        }
        val currancyCode = intent.getSerializableExtra("currencyCode") as String?
        val currencySymbol = intent.getSerializableExtra("currencySymbol") as String?
         groupId = (intent.getSerializableExtra("groupId") as String?).toString()
        groupName = (intent.getSerializableExtra("groupName") as String?).toString()

        expenceType = (intent.getSerializableExtra("expenceType") as String?).toString()
        companyNumber = (intent.getSerializableExtra("company") as String?).toString()
        department = (intent.getSerializableExtra("department") as String?).toString()
         taxAmount = ((intent.getSerializableExtra("taxAmount") as Double?)!!)
        var mtotalAmount = ((intent.getSerializableExtra("totalAmount") as String?)!!)
        totalAmountForsplit = ((intent.getSerializableExtra("totalAmount") as String?)!!).toDouble()
        try {
           // binding.edtTotalAmount.setText(intent.getSerializableExtra("totalAmount") as String?)
            binding.edtTotalAmount.setText(String.format("%.2f",mtotalAmount.toDouble()))
            binding.edtTax.setText(String.format("%.2f",taxAmount.toString().toDouble()))

        } catch (e: Exception) {
        }
       // binding.edtTax.setText(taxAmount.toString())

        if (currencySymbol != null) {
            binding.tvTotalAmountCurrency.text = currencySymbol
            binding.tvTaxCurrency.text = currencySymbol
           // binding.edtTax.setCurrencySymbol(currencySymbol, useCurrencySymbolAsHint = true)
        }
        if (currancyCode != null) {
           // binding.edtTotalAmount.setLocale(currancyCode)
           // binding.edtTax.setLocale(currancyCode)
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
       // viewModel.expenseGroupList = dropdownResponse.expenseGroup as MutableList<ExpenseGroup>
        viewModel.expenseGroupList.clear()

        ( dropdownResponse.expenseGroup as MutableList<ExpenseGroup>).forEachIndexed { index, expenseGroup ->

            if(!expenseGroup.name.contains("Mileage")){
                viewModel.expenseGroupList.add(expenseGroup)
            }
        }
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

        // Company List Adapter
        val companyAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.companyList
        )
        binding.spnCompany.adapter = companyAdapter
        var compnypostion=0

        viewModel.companyList.forEachIndexed { index, element ->
            if(companyNumber == element.id){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompany.setSelection(compnypostion)

        binding.spnCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                compnyId=viewModel.companyList[position].id.toInt()
                viewModel.departmentList.clear()

                binding.spnExpenseGroup.adapter=null
                binding.edtGroupValue.setText(" ")
                setupExpenceGroupType()
                if(groupName == "Capital Asset"){
                    viewModel.departmentListCompany.forEach {
                        if(it.name == "Tangible Assets"&&it.company_id==compnyId.toString() ){
                            viewModel.departmentList.add(it)
                            return@forEach
                        }
                    }
                    binding.spnDepartment.setBackgroundResource(R.drawable.spinner_bg)

                }else{
                    viewModel.departmentListCompany.forEach {
                        //  println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                        if(it.company_id == compnyId.toString()){
                            viewModel.departmentList.add(it)
                        }
                    }
                }



               /* viewModel.departmentListCompany.forEach {
                    println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                    if(it.company_id == compnyId.toString()){
                        viewModel.departmentList.add(it)
                    }
                }*/
                setupDeparmentType()

                // System.out.println("selected appoint :"+ viewModel.companyList[position].code)
                //binding.edtTitle.setText(viewModel.companyList[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


        setupExpenceTypeData(groupId)

    }
    private fun setupExpenceTypeData(groupid:String){
        viewModel.expenseTypeList.clear()
        binding.edtExpenceTypeValue.setText(" ")
        viewModel.expenseTypeListExpenseGroup.forEach {

            if (it.expenseGroupID == groupid) {
                viewModel.expenseTypeList.add(it)
                // println("selected expenseTypeList Added :" )

            } else if (it.companyID.isNullOrEmpty()) {
                //viewModel.expenseTypeList.add(it)

            }


           /* if (it.expenseGroupID == groupid && (it.companyID == compnyId.toString() || it.companyID == null)) {
                viewModel.expenseTypeList.add(it)
                // println("selected expenseTypeList Added :" )

            } else if (it.companyID.isNullOrEmpty()) {
                //viewModel.expenseTypeList.add(it)

            }*/

        }
        setupExpenceType()
    }
    private fun setupExpenceGroupType(){
        // Expense Group Adapter
        // viewModel.expenseGroupList.add(ExpenseGroup("0","N/A","0","active"))
        val expenseGroupAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.expenseGroupList
        )
        binding.spnExpenseGroup.adapter = expenseGroupAdapter

        var grouppos=0

        viewModel.expenseGroupList.forEachIndexed { index, element ->
            if(groupId == element.id){
                grouppos=index
                return@forEachIndexed
            }
        }
        binding.spnExpenseGroup.setSelection(grouppos)
        binding.spnExpenseGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    binding.edtGroupValue.setText(viewModel.expenseGroupList[position].name)
                    val groupid = viewModel.expenseGroupList[position].id
                    println("selected group ID :$groupid")
                    viewModel.expenseTypeList.clear()
                    binding.edtExpenceTypeValue.setText(" ")
                    viewModel.expenseTypeListExpenseGroup.forEach {

                        if (it.expenseGroupID == groupid && (it.companyID == compnyId.toString() || it.companyID == null)) {
                            viewModel.expenseTypeList.add(it)
                            // println("selected expenseTypeList Added :" )

                        } else if (it.companyID.isNullOrEmpty()) {
                            //viewModel.expenseTypeList.add(it)

                        }

                    }
                    setupExpenceType()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(this@SplitClaimActivity2, "Nothing selected", Toast.LENGTH_SHORT).show();

            }
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

        var postion=0
        viewModel.expenseTypeList.forEachIndexed { index, element ->
            if (expenceType == element.id) {
                postion = index
                return@forEachIndexed
            }
        }
        binding.spnExpense.setSelection(postion)
        binding.spnExpense.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    binding.edtExpenceTypeValue.setText(viewModel.expenseTypeList.get(position).name)
                    expenseCode = viewModel.expenseTypeList.get(position).activityCode
                    mtaxcodeId = viewModel.expenseTypeList[position].taxCodeID
                    setupTax()


                    if (binding.edtAutionValue.text.toString().isNotEmpty()) {
                        binding.tvAuctionExpCode.text = expenseCode

                    } else {
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
        var postion=0
        viewModel.departmentList.forEachIndexed { index, element ->

            if(department == element.id){
                postion=index
                return@forEachIndexed
            }
        }
        binding.spnDepartment.setSelection(postion)
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