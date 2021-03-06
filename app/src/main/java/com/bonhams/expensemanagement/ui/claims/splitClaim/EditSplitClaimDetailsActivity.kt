package com.bonhams.expensemanagement.ui.claims.splitClaim

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
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.splitClaim.EditSplitClaimDetailsFragment.Companion.splitItmlist
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.totalAmount
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class EditSplitClaimDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivitySplitClaimBinding
    private lateinit var viewModel: NewClaimViewModel
    private var expenseCode: String = ""
    private var mtaxcodeId: String = ""
    private var taxcodeId: String = ""
    private var taxcodeValue: String = ""
    private var compnyId: Int = 0
    private var selectedPostion: Int = 0
    private var groupId: String = ""
    public var taxAmount: Double=0.00

    var selectedGroupID="0"
    var selectedCompanyCode="n/a"
    var selectedExpenceTypeId="-1"
    var selectedSplitId=""
    var selectedTaxcode=""
    var selecteddepartment=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_split_claim)
        binding.lifecycleOwner = this
        val item = intent.getSerializableExtra("SplitItem") as SplitClaimItem?
        val currancyCode = intent.getSerializableExtra("currencyCode") as String?
        val currencySymbol = intent.getSerializableExtra("currencySymbol") as String?
         selectedPostion = (intent.getSerializableExtra("postion") as Int?)!!
        selectedCompanyCode=item?.companyCode?:"n/a"
        selectedExpenceTypeId=item?.expenseType?:"0"
        selectedGroupID=item?.expense_group_id?:"0"
        selectedSplitId=item?.split_id?:"0"
        selectedTaxcode=item?.taxCodeValue?:"00"
        selecteddepartment=item?.department?:"0"
        binding.edtTotalAmount.setText(item?.totalAmount)
        binding.edtTax.setText(item?.tax.toString())
        binding.edtAutionValue.setText(item?.auctionSales)
        binding.tvAuctionExpCode.text = item?.expenceCode
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
                        companyOne?.code?:"0",
                        companyOne?.code?:"0", departmentOne?.id?:"0", expenseTypeOne?.id!!,
                        totalAmountOne.toString(),
                        taxcodeId, mtax,companyOne?.name?:"",departmentOne?.name?:"",
                        expenseTypeOne.name,binding.edtAutionValue.text.toString(),binding.tvAuctionExpCode.text.toString(),
                        expenseTypeOne.expenseCodeID,taxcodeValue,selectedSplitId,selectedGroupID
                    )
                    println("item finish:$splitOne")
                    splitItmlist.removeAt(selectedPostion)
                    splitItmlist.add(selectedPostion,splitOne)
                    //splitItmlist.add(splitOne)




                } catch (e: Exception) {
                }
                finish()

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
        val taxAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.taxList
        )
        binding.spntaxcode.adapter = taxAdapter
        var postion=0
        viewModel.taxList.forEachIndexed { index, element ->
            if(element.tax_code == selectedTaxcode) {
                postion=index
                return@forEachIndexed
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
        //val mremaningAmount=String.format("%.2f",remaningAmount).toDouble()
       /* return if(amount<= mremaningAmount){
            true
        }else {
            Toast.makeText(this, "Please Enter The Amount below ${mremaningAmount}", Toast.LENGTH_SHORT)
                .show()
            false

        }*/
       return true
    }
    private fun setDropdownDataObserver() {

        val currancyCode = intent.getSerializableExtra("currencyCode") as String?
        val currencySymbol = intent.getSerializableExtra("currencySymbol") as String?
       //  groupId = (intent.getSerializableExtra("groupId") as String?).toString()
         groupId = selectedGroupID
         //taxAmount = ((intent.getSerializableExtra("taxAmount") as Double?)!!)
        //binding.edtTax.setText(taxAmount.toString())
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
        System.out.println("selectedCompanyCode in edit:"+selectedCompanyCode)
            viewModel.companyList.forEachIndexed { index, element ->
                if (selectedCompanyCode == element.code) {
                    compnypostion = index
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
                viewModel.departmentListCompany.forEach {
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


        setupExpenceTypeData(groupId)

    }
    private fun setupExpenceTypeData(groupid:String){
        viewModel.expenseTypeList.clear()
        binding.edtExpenceTypeValue.setText(" ")
        viewModel.expenseTypeListExpenseGroup.forEach {
           // viewModel.expenseTypeList.add(it)

           /* if (it.expenseGroupID == groupid && (it.companyID == compnyId.toString() || it.companyID == null)) {
                viewModel.expenseTypeList.add(it)
                // println("selected expenseTypeList Added :" )

            } else if (it.companyID.isNullOrEmpty()) {
                //viewModel.expenseTypeList.add(it)

            }*/
            if (it.expenseGroupID == groupid ) {
                viewModel.expenseTypeList.add(it)
                // println("selected expenseTypeList Added :" )

            } else if (it.companyID.isNullOrEmpty()) {
                //viewModel.expenseTypeList.add(it)

            }

        }
        setupExpenceType(false)
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
                    setupExpenceType(true)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(this@EditSplitClaimDetailsActivity, "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }

    private fun setupExpenceType(isShowDefault:Boolean){
        var isDefaultShow=isShowDefault
        // Expense Type Adapter

        val expenseTypeAdapter = CustomSpinnerAdapter(
            this,
            R.layout.item_spinner,
            viewModel.expenseTypeList
        )
        binding.spnExpense.adapter = expenseTypeAdapter

        var postion=0
        viewModel.expenseTypeList.forEachIndexed { index, element ->
            if (selectedExpenceTypeId == element.id) {
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


                    if (!binding.edtAutionValue.text.toString().isEmpty()) {
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

            if(selecteddepartment == element.cost_code){
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