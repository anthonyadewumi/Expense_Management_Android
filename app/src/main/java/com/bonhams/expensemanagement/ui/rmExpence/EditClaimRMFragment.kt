package com.bonhams.expensemanagement.ui.rmExpence

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.model.Currency
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.MainClaim
import com.bonhams.expensemanagement.databinding.FragmentEditClaimBinding
import com.bonhams.expensemanagement.databinding.FragmentNewClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.claimedit.EditClaimViewModel
import com.bonhams.expensemanagement.ui.claims.claimDetail.claimedit.EditClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.splitClaim.EditSplitClaimDetailsFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimDetailsFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragmentEdit
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.*
import java.util.*


class EditClaimRMFragment() : Fragment() ,RecylerCallback{

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var claimDetail2: ClaimDetailsResponse
    private lateinit var claimDetail: MainClaim
    private lateinit var viewModel: EditClaimViewModel
    private lateinit var binding: FragmentEditClaimBinding

    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private lateinit var refreshPageListener: RefreshPageListener
    private var shouldRefreshPage: Boolean = false
    private var expenseCode: String = ""
    private var mtaxcodeId: String = ""
    private var taxcodeId: String = ""
    private var compnyId: Int = 0
    private var currencyCode: String = ""
    private var currencySymbol: String = ""
    private var companyDateFormate: String = ""
    private var companyLocation: String = ""
    private var isCreateCopy: Boolean = false
    private lateinit var splitedClaimDetails: ClaimDetailsResponse
    private lateinit var dropDownResponse: DropdownResponse
    private var dateofRecipt: String = ""
    var groupname="n/a"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_claim, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setClickListeners()
        setupAttachmentRecyclerView()
        setDropdownDataObserver()
        setupView()
        setupTextWatcher()
        getDetails()

        return view
    }

    fun setClaimDetails(detail: ClaimDetailsResponse?){
        detail?.let {
            claimDetail2 = it
            claimDetail = it.main_claim!!
        }
    }

    fun setRefreshPageListener(refreshListener: RefreshPageListener){
        refreshPageListener = refreshListener
    }

    private fun setupView(){

        try {
            if (this::claimDetail.isInitialized) {
                isCreateCopy=true
                println("isCopyClaim total amount :"+claimDetail.totalAmount)

                /*  binding.edtTitle.setText(
                      claimDetail.title.replaceFirstChar(Char::uppercase) ?: claimDetail.title
                  )*/
                binding.edtMerchantName.setText(
                    claimDetail.merchant.replaceFirstChar(Char::uppercase) ?: claimDetail.merchant
                )
                binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                    claimDetail.date_of_receipt,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                )
                dateofRecipt = Utils.getFormattedDate2(
                    claimDetail.date_of_receipt,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
                )
//            binding.edtCompanyNumber.setText(claimDetail.companyName)
              /*  binding.tvDateOfSubmission.text = if (!claimDetail.date_of_receipt.trim().isNullOrEmpty())
                    Utils.getFormattedDate(claimDetail.createdOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,companyDateFormate
                    ) else "n/a"*/
               // binding.edtTotalAmount.setText(claimDetail.totalAmount)
               // binding.edtTax.setText(claimDetail.tax)
                // binding.tvNetAmount.text = claimDetail.netAmount
              //  binding.tvNetAmount.setText(claimDetail.netAmount)

                binding.edtDescription.setText(claimDetail.description)
                if (!claimDetail.attachments.isNullOrEmpty() && claimDetail.attachments.trim()
                        .isNotEmpty()
                ) {
                    /*val attachment=claimDetail.attachments.split(",")
                    viewModel.attachmentsList.clear()
                    attachment.forEach {
                        viewModel.attachmentsList.add(it)
                    }

                    viewModel.attachmentsList = mutableListOf(claimDetail.attachments)*/
                }
                refreshAttachments()

            }else{
                binding.edtMerchantName.setText(AppPreferences.ledgerId)
                viewModel.attachmentsList.clear()
            }
            refreshAttachments()
        }
        catch (error: Exception){
            Log.e(TAG, "setupView: ${error.message}")
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity(),
            EditClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(EditClaimViewModel::class.java)
    }

    private fun setClickListeners(){
        binding.edtGroupValue.setOnClickListener {
            binding.spnExpenseGroup.performClick()
        }
        binding.edtExpenceTypeValue.setOnClickListener {
            binding.spnExpenseType.performClick()
        }

        binding.tvUploadPic.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })
        binding.ivPicUpload.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })

        binding.tvDateOfSubmission.setOnClickListener(View.OnClickListener {
            showCalenderDialog()
        })

        binding.btnSplit.setOnClickListener(View.OnClickListener {
            splitNewClaim()
        })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                if(NoInternetUtils.isConnectedToInternet(it))
                    createNewClaim()
                else
                    Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinners(){
       // Tax Adapter
        val taxAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.taxList
        )
        binding.spntaxcode.adapter = taxAdapter
        binding.spntaxcode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {}
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
        when (AppPreferences.userType) {
            "Reporting Manager" -> { }
            "Finance Department" -> { }
            "Admin" -> { }
            "Final Approver" -> { }
            else->{
                viewModel.companyList.forEach {
                    if(AppPreferences.company == it.name){
                        viewModel.companyList= listOf(it)
                        return@forEach
                    }
                }
            }
        }
        val companyAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_spinner, viewModel.companyList)
        binding.spnCompanyNumber.adapter = companyAdapter
        var compnypostion=0
        viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.company == element.name){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompanyNumber.setSelection(compnypostion)

        binding.spnCompanyNumber.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                compnyId=viewModel.companyList[position].id.toInt()
                companyDateFormate=viewModel.companyList[position].dateFormat
                companyLocation=viewModel.companyList[position].location
                binding.spnExpenseGroup.adapter=null

                if(isCreateCopy){
                    setupExpenceGroupTypeCopy(claimDetail.expenseGroupID)

                    binding.edtTotalAmount.setText(claimDetail.totalAmount)
                    binding.edtTax.setText(claimDetail.tax)
                    binding.tvNetAmount.setText(claimDetail.netAmount)
                   // binding.tvDateOfSubmission.setText("")
                    binding.edtGroupValue.setText(claimDetail.expenseGroupName)
                    binding.edtExpenceTypeValue.setText(claimDetail.expenseTypeName)

                }else {
                    setupExpenceGroupType(true)

                    binding.edtTotalAmount.setText("")
                    binding.edtTax.setText("0")
                    binding.tvNetAmount.setText("")
                   // binding.tvDateOfSubmission.text = ""
                    binding.edtGroupValue.setText("")

                }


                println("selected company currency id :"+ viewModel.companyList[position].currency_type_id)
                println("selected company date format:"+ viewModel.companyList[position].dateFormat)
                println("selected company location:"+ viewModel.companyList[position].location)
                viewModel.departmentList.clear()
                viewModel.departmentListCompany.forEach {
                    println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                    if(it.company_id == compnyId.toString()){
                        viewModel.departmentList.add(it)
                    }
                }
                setupDeparmentType()
                viewModel.currencyList.forEach {
                    if(it.id.toInt()==claimDetail.currencyTypeID.toInt()){
                        val symbol=it.symbol
                        val code=it.code
                        currencyCode=code
                        currencySymbol=symbol
                        binding.tvTotalAmountCurrency.text = symbol
                        binding.tvTaxAmountCurrency.text = symbol
                        binding.tvNetAmountCurrency.text = symbol


                        if(isCreateCopy){
                            val currency: Currency? =
                                viewModel.currencyList.find { it.id.toInt() == claimDetail.currencyTypeID.toInt() }
                            val currencyPos = viewModel.currencyList.indexOf(currency)
                            if (currencyPos >= 0) {
                                binding.spnCurrency.setSelection(currencyPos)
                            }
                        }else{
                            val currency: Currency? =
                                viewModel.currencyList.find { it.id.toInt() == viewModel.companyList[position].currency_type_id }
                            val currencyPos = viewModel.currencyList.indexOf(currency)
                            if (currencyPos >= 0) {
                                binding.spnCurrency.setSelection(currencyPos)
                            }
                        }



                      /*  val currency: Currency? =
                            viewModel.currencyList.find { it.id.toInt() == viewModel.companyList[position].currency_type_id }
                        val currencyPos = viewModel.currencyList.indexOf(currency)
                        if (currencyPos >= 0) {
                            binding.spnCurrency.setSelection(currencyPos)
                        }*/
                    }
                }
                binding.edtTitle.setText(viewModel.companyList[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }



        // Currency Adapter
        val currencyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.currencyList
        )
        binding.spnCurrency.adapter = currencyAdapter

        binding.spnCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //binding.edtTotalAmount.text = null
               // binding.edtTax.text = null
               // binding.tvNetAmount.text = null
              //  binding.edtTotalAmount.clearFocus()
               // binding.edtTax.clearFocus()
               // binding.tvNetAmount.clearFocus()

                val code = viewModel.currencyList[position].code
                val symbol = viewModel.currencyList[position].symbol
                currencyCode=code
                currencySymbol=symbol
               // binding.edtTotalAmount.text = null
               // binding.edtTax.text = null
               // binding.tvNetAmount.text = null
               // binding.edtTotalAmount.clearFocus()
               // binding.edtTax.clearFocus()
              //  binding.tvNetAmount.clearFocus()
                binding.tvTotalAmountCurrency.text = symbol
                binding.tvTaxAmountCurrency.text = symbol
                binding.tvNetAmountCurrency.text = symbol

              //  binding.edtTotalAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
               // binding.edtTotalAmount.setLocale(code)

              //  binding.edtTax.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
               // binding.edtTax.setLocale(code)

               // binding.tvNetAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
               // binding.tvNetAmount.setLocale(code)


            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        if(this::claimDetail.isInitialized){
            try {
                /*val expenseGroup: ExpenseGroup? =
                    viewModel.expenseGroupList.find { it.id == claimDetail.expenseGroupID }
                val expenseGroupPos = viewModel.expenseGroupList.indexOf(expenseGroup)//(currencyAdapter).getPosition(expenseGroup)
                if (expenseGroupPos >= 0) {
                    binding.spnExpenseGroup.setSelection(expenseGroupPos)
                }
                binding.edtGroupValue.setText(expenseGroup?.name)*/

               /* val expenseType: ExpenseType? =
                    viewModel.expenseTypeList.find { it.id == claimDetail.expenseTypeID }
                val expenseTypePos = viewModel.expenseTypeList.indexOf(expenseType)
                if (expenseTypePos >= 0) {
                    binding.spnExpenseType.setSelection(expenseTypePos)
                }
                binding.edtExpenceTypeValue.setText(expenseType?.name)*/
                val company: Company? =
                    viewModel.companyList.find { it.name == claimDetail.companyName }
                val companyPos = viewModel.companyList.indexOf(company)
                if (companyPos >= 0) {
                    binding.spnCompanyNumber.setSelection(companyPos)
                }

                val department: Department? =
                    viewModel.departmentList.find { it.name == claimDetail.department }
                val departmentPos = viewModel.departmentList.indexOf(department)
                if (departmentPos >= 0) {
                    binding.spnDepartment.setSelection(departmentPos)
                }

                val currency: Currency? =
                    viewModel.currencyList.find { it.id == claimDetail.currencyTypeID }
                val currencyPos = viewModel.currencyList.indexOf(currency)
                if (currencyPos >= 0) {
                    binding.spnCurrency.setSelection(currencyPos)
                }

                binding.edtTotalAmount.setText(String.format("%.2f", claimDetail.totalAmount.toDouble()))
                binding.edtTax.setText(String.format("%.2f", claimDetail.tax.toDouble()))
                binding.tvNetAmount.setText(String.format("%.2f", claimDetail.netAmount.toDouble()))
                println("auction create copy "+claimDetail.auction)
                binding.edtAutionValue.setText(claimDetail.auction.toString())
                binding.tvAuctionExpCode.text = claimDetail.expenseCode

            }
            catch (e: Exception){
                Log.e(TAG, "setupSpinners: ${e.message}")
            }
        }
    }

    private fun setupExpenceGroupType(isShowDefault:Boolean){
        var isDefaultshow=isShowDefault
        // Expense Group Adapter
        // viewModel.expenseGroupList.add(ExpenseGroup("0","N/A","0","active"))
        val expenseGroupAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.expenseGroupList
        )
        binding.spnExpenseGroup.adapter = expenseGroupAdapter
        binding.spnExpenseGroup.isSelected=false
        binding.spnExpenseGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if(!isDefaultshow) {
                    binding.edtGroupValue.setText(viewModel.expenseGroupList[position].name)
                    val groupid = viewModel.expenseGroupList[position].id
                    println("selected group ID :$groupid")
                     groupname = viewModel.expenseGroupList[position].name
                    println("selected group ID :$groupid")
                    if(groupname == "Capital Asset"){
                        viewModel.departmentList.clear()
                        viewModel.departmentListCompany.forEach {
                            if(it.name == "Tangible Assets"&&it.company_id==compnyId.toString() ){
                                viewModel.departmentList.add(it)
                                return@forEach
                            }
                        }
                        binding.spnDepartment.setBackgroundResource(R.drawable.spinner_bg)

                        setupDeparmentType()
                    }else{
                        viewModel.departmentList.clear()
                        viewModel.departmentListCompany.forEach {
                            if(it.company_id == compnyId.toString()){
                                viewModel.departmentList.add(it)
                            }
                        }
                        binding.spnDepartment.setBackgroundResource(R.drawable.spinner_purple_bg)

                        setupDeparmentType()
                    }
                    viewModel.expenseTypeList.clear()
                    binding.edtExpenceTypeValue.setText(" ")
                    viewModel.expenseTypeList.add(ExpenseType("0","Select Expense Type",""))
                    viewModel.expenseTypeListExpenseGroup.forEach {
                       // if (it.expenseGroupID == groupid && (it.companyID == compnyId.toString() || it.companyID == null)) {


                            if (it.expenseGroupID == groupid) {
                            viewModel.expenseTypeList.add(it)
                            // println("selected expenseTypeList Added :" )

                        } else if (it.companyID.isNullOrEmpty()) {
                            //viewModel.expenseTypeList.add(it)

                        }

                    }
                    setupExpenceType(true)
                }else{
                    isDefaultshow=false
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }

    private fun setupExpenceGroupTypeCopy(groupId:String){
        // Expense Group Adapter
        val expenseGroupAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.expenseGroupList
        )
        binding.spnExpenseGroup.adapter = expenseGroupAdapter
        val expenseGroup: ExpenseGroup? =
            viewModel.expenseGroupList.find { it.id == groupId }
        val expenseGroupPos = viewModel.expenseGroupList.indexOf(expenseGroup)
        if (expenseGroupPos >= 0) {
            binding.spnExpenseGroup.setSelection(expenseGroupPos)
        }
        binding.spnExpenseGroup.isSelected=false
        binding.spnExpenseGroup.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    binding.edtGroupValue.setText(viewModel.expenseGroupList[position].name)
                    val groupid = viewModel.expenseGroupList[position].id
                    println("selected group ID :$groupid")
                val groupname = viewModel.expenseGroupList[position].name
                println("selected group ID :$groupid")
                if(groupname == "Capital Asset"){
                    viewModel.departmentList.clear()
                    viewModel.departmentListCompany.forEach {
                        if(it.name == "Tangible Assets"&&it.company_id==compnyId.toString() ){
                            viewModel.departmentList.add(it)
                            return@forEach
                        }
                    }
                    binding.spnDepartment.setBackgroundResource(R.drawable.spinner_bg)

                    setupDeparmentType()
                }else{
                    viewModel.departmentList.clear()
                    viewModel.departmentListCompany.forEach {
                        if(it.company_id == compnyId.toString()){
                            viewModel.departmentList.add(it)
                        }
                    }
                    binding.spnDepartment.setBackgroundResource(R.drawable.spinner_purple_bg)

                    setupDeparmentType()
                }
                    viewModel.expenseTypeList.clear()
                    binding.edtExpenceTypeValue.setText(" ")
                    viewModel.expenseTypeList.add(ExpenseType("0","Select Expense Type",""))
                    viewModel.expenseTypeListExpenseGroup.forEach {
                        // if (it.expenseGroupID == groupid && (it.companyID == compnyId.toString() || it.companyID == null)) {


                        if (it.expenseGroupID == groupid) {
                            viewModel.expenseTypeList.add(it)
                            // println("selected expenseTypeList Added :" )

                        } else if (it.companyID.isNullOrEmpty()) {
                            //viewModel.expenseTypeList.add(it)

                        }

                    }
                    setupExpenceType(false)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }


    private fun setupExpenceType(isShowDefault:Boolean){
        var isDefaultShow=isShowDefault
        // Expense Type Adapter
        println("call expence type method ")

        val expenseTypeAdapter = CustomSpinnerAdapter(
                requireContext(),
                R.layout.item_spinner,
                viewModel.expenseTypeList
            )
            binding.spnExpenseType.adapter = expenseTypeAdapter
        binding.spnExpenseType.isSelected=false

        if(isCreateCopy){
             var expenseTypePos = 0
            viewModel.expenseTypeList.forEachIndexed { index, expenseType ->

                if(expenseType.name==claimDetail.expenseTypeName){
                    expenseTypePos=index
                }
                }
                if (expenseTypePos >= 0) {
                    binding.spnExpenseType.setSelection(expenseTypePos)
                }
             }


        binding.spnExpenseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    binding.edtExpenceTypeValue.setText(viewModel.expenseTypeList[position].name)
                    expenseCode = viewModel.expenseTypeList[position].activityCode
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
    private fun setupDeparmentType(){
        // Department Adapter
        val departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
       var postion=0
        viewModel.departmentList.forEachIndexed { index, element ->

            if(AppPreferences.departmentID == element.id){
                postion=index
                return@forEachIndexed
            }
        }
        binding.spnDepartment.setSelection(postion)

    }

    private fun setupTax(){

        viewModel.taxList.forEach {

            if(it.id.toString() == mtaxcodeId) {


                binding.edtTaxcode.setText(it.tax_code)

            }
        }

        val taxAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.taxList
        )
        binding.spntaxcode.adapter = taxAdapter

        var postion=0
        viewModel.taxList.forEachIndexed { index, element ->
                 if (element.tax_code == claimDetail.tax_code) {
                    postion = index
                    return@forEachIndexed
                }

        }
        binding.spntaxcode.setSelection(postion)

        binding.spntaxcode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                taxcodeId= viewModel.taxList[position].id.toString()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }
    private fun setupTextWatcher(){

     //   binding.edtTotalAmount.addTextChangedListener(NumberTextWatcher(binding.edtTotalAmount, "#,###"))
        binding.edtTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtTotalAmount.text.isNotEmpty()&&binding.edtTax.text.isNotEmpty())
                    updateNetAmount(binding.edtTotalAmount.text.toString(), binding.edtTax.text.toString())

            }
        })

        binding.edtTax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtTotalAmount.text.isNotEmpty()&&binding.edtTax.text.isNotEmpty())
                updateNetAmount(binding.edtTotalAmount.text.toString(), binding.edtTax.text.toString())
            }
        })
        binding.edtAutionValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
              if(binding.edtAutionValue.text.toString().isNotEmpty()){
                  binding.tvAuctionExpCode.text = expenseCode

              }else{
                  binding.tvAuctionExpCode.text = ""
              }
            }
        })
    }

    private fun setupAttachmentRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttachments.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"claim",this)
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun refreshAttachments(){
        println("call refreshAttachments item:${viewModel.attachmentsList.size}")

        if(viewModel.attachmentsList.size > 0){
            binding.tvNoFileSelected.visibility = View.GONE
            binding.rvAttachments.visibility = View.VISIBLE
            attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"claim",this)
            binding.rvAttachments.adapter = attachmentsAdapter
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            binding.rvAttachments.visibility = View.GONE
            binding.tvNoFileSelected.visibility = View.VISIBLE
        }
    }

    private fun updateNetAmount(total: String, tax: String){
        try {
            var totalAmount = 0.0
            var taxAmount = 0.0

            if (!total.isNullOrEmpty()) {
                totalAmount = total.toDouble()
            }
            if (!tax.isNullOrEmpty()) {
                taxAmount = tax.toDouble()
            }

            if(taxAmount>totalAmount){
                Toast.makeText(contextActivity, "The tax amount must not be greater than the total amount", Toast.LENGTH_SHORT).show()

                binding.tvNetAmount.setText("")

            }else {
                val netAmount = totalAmount - taxAmount

                if(netAmount<0){
                    Toast.makeText(contextActivity, "Net amount should be greater than 0", Toast.LENGTH_SHORT).show()
                    binding.tvNetAmount.setText("")

                }else{
                    binding.tvNetAmount.setText(String.format("%.2f", netAmount))

                }

                // binding.tvNetAmount.text = "$netAmount"
                //binding.tvNetAmount.text =
            }
        }
        catch (error: Exception){
            Log.e(TAG, "updateNetAmount: ${error.message}")
        }
    }

    private fun setDropdownDataObserver() {
        viewModel.getDropDownData().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                initializeSpinnerData(response)
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

    private fun initializeSpinnerData(dropdownResponse: DropdownResponse){
        dropDownResponse=dropdownResponse
       // viewModel.expenseGroupList = dropdownResponse.expenseGroup as MutableList<ExpenseGroup>
        viewModel.expenseGroupList.clear()
       viewModel.expenseGroupList.add(ExpenseGroup("0","Select Expense Group","0","active"))

        ( dropdownResponse.expenseGroup as MutableList<ExpenseGroup>).forEachIndexed { index, expenseGroup ->

            if(!expenseGroup.name.contains("Mileage")){
                viewModel.expenseGroupList.add(expenseGroup)
            }
        }

       // viewModel.expenseTypeList = dropdownResponse.expenseType
        viewModel.expenseTypeListExpenseGroup = dropdownResponse.expenseType
        viewModel.departmentListCompany = dropdownResponse.departmentList
        viewModel.currencyList  = dropdownResponse.currencyType as MutableList<Currency>
        viewModel.carTypeList  = dropdownResponse.carType
        viewModel.statusTypeList  = dropdownResponse.statusType
        viewModel.mileageTypeList  = dropdownResponse.mileageType
        viewModel.companyList  = dropdownResponse.companyList
        viewModel.taxList  = dropdownResponse.tax

        setupSpinners()
    }

    private fun setCreateClaimObserver(newClaimRequest: NewClaimRequest) {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("claim_type", "E")

        for (photoPath in viewModel.attachmentsList) {
            if (photoPath != null) {
                val images = File(photoPath)
                if (images.exists()) {
                    //val bitmap = BitmapFactory.decodeFile(photoPath)
                  // val imgFile= bitmapToFile(bitmap,images.name)
                    //builder.addFormDataPart("claimImage", images.name, RequestBody.create(MultipartBody.FORM,imgFile))
                   builder.addFormDataPart("claimImage", images.name, RequestBody.create(MultipartBody.FORM, images))
                }
            }
        }
        val mrequestBody: RequestBody = builder.build()

        viewModel.uploadClaimAttachement(mrequestBody).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                               // newClaimRequest.attachments=response.images
                                //callApiCreateClaim(newClaimRequest)


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })

        /*val mapRequestBody = LinkedHashMap<String, RequestBody>()

        val requestBody: RequestBody
        val body: MultipartBody.Part
        val arrBody: MutableList<MultipartBody.Part> = ArrayList()
        val file= File( viewModel.attachmentsList.get(0))
        requestBody = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file)



        mapRequestBody["file\"; filename=\"" + file] = requestBody
        mapRequestBody["title"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.title.toString())
        mapRequestBody["merchantName"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.merchantName.toString())
        mapRequestBody["expenseGroup"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.expenseGroup.toString())
        mapRequestBody["expenseType"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.expenseType.toString())

        mapRequestBody["companyNumber"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.companyNumber.toString())
        mapRequestBody["department"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.department.toString())
        mapRequestBody["dateSubmitted"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.dateSubmitted.toString())
        mapRequestBody["currency"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.currency.toString())

        mapRequestBody["totalAmount"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.totalAmount.toString())
        mapRequestBody["tax"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.tax.toString())
        mapRequestBody["netAmount"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.netAmount.toString())
        mapRequestBody["description"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.description.toString())
        mapRequestBody["taxCode"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.taxCode.toString())
        mapRequestBody["auction"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.auction.toString())
        mapRequestBody["expenseCode"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), newClaimRequest.expenseCode.toString())
        mapRequestBody["claim_type"] = RequestBody.create(okhttp3.MediaType.parse("text/plain"), "E")


        mapRequestBody["split[]"] = RequestBody.create(okhttp3.MediaType.parse("application/json"), Gson().toJson(SplitClaimDetail("12")))
        body = MultipartBody.Part.createFormData("attachments", file.name, requestBody)
        arrBody.add(body)
        viewModel.uploadClaim(arrBody).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
*/
       /*viewModel.createNewClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                                //Toast.makeText(contextActivity, "Claim added successfully/submitted successfully", Toast.LENGTH_SHORT).show()

                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })*/
    }
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
            file?.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
    private fun callApiEditClaim(newClaimRequest: JsonObject){
        viewModel.editClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "editClaimObserver: ${resource.status}")
                                //Toast.makeText(contextActivity, "Claim added successfully/submitted successfully", Toast.LENGTH_SHORT).show()

                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "editClaimObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun createNewClaim() {
        try {

            if (binding.tvDateOfSubmission.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please select Date", Toast.LENGTH_LONG).show()
                onCreateClaimFailed()
                return
            }
            if (binding.edtTotalAmount.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please Enter Amount", Toast.LENGTH_LONG).show()

                onCreateClaimFailed()
                return
            }
            if (binding.edtTax.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please Enter Tax", Toast.LENGTH_LONG).show()

                onCreateClaimFailed()
                return
            }
            val newClaimRequest = getEditClaimRequest()

            callApiEditClaim(newClaimRequest)

            /*if(viewModel.attachmentsList.size > 0){
                binding.btnSubmit.visibility = View.GONE
                setCreateClaimObserver(newClaimRequest)
            } else{
                Toast.makeText(contextActivity, "Please select receipt image to upload", Toast.LENGTH_LONG).show()
                return
            }*/


        }
        catch (e: Exception){
            Log.e(TAG, "createNewClaim: ${e.message}")
        }
    }
    private fun getClaimRequest() : NewClaimRequest{

        dateofRecipt = Utils.getFormattedDate2(
            claimDetail.date_of_receipt,
            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
        )

        /*var dateFormate = if(companyDateFormate=="USA") {
            Constants.MMM_DD_YYYY_FORMAT
        }else{
            Constants.DD_MM_YYYY_FORMAT

        }*/
        return viewModel.getNewClaimRequest(
            binding.edtTitle.text.toString().trim(),
            binding.edtMerchantName.text.toString().trim(),
            if(binding.edtGroupValue.text.isEmpty())
            {
                ""
            }else{
                if (!viewModel.expenseGroupList.isNullOrEmpty()) viewModel.expenseGroupList[binding.spnExpenseGroup.selectedItemPosition].id else ""

            },
            if(binding.edtExpenceTypeValue.text.isEmpty())
            {
                ""
            }else{
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else ""

            },
            if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyNumber.selectedItemPosition].id else "",
//            binding.edtCompanyNumber.text.toString().trim(),
            if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "",
            dateofRecipt,
            if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "",
            binding.edtTotalAmount.text.toString(),
            binding.edtTax.text.toString(),
            binding.tvNetAmount.text.toString(),
            binding.edtDescription.text.toString().trim(),
            if (!taxcodeId.isNullOrEmpty()) taxcodeId else "",
            binding.edtAutionValue.text.toString().trim(),
            if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "",

            viewModel.claimImageList as List<String>,
            viewModel.attachmentsList as List<String>
        )
    }
    private fun splitNewClaim() {
        //  attachments = viewModel.attachmentsList.joinToString { it }
        try {
            val newClaimRequest = getEditClaimRequestForSplit()

          /*  if (!validateCreateClaim(newClaimRequest)) {
                onCreateClaimFailed()
                return
            }*/
            /*if (binding.tvDateOfSubmission.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please select Date", Toast.LENGTH_LONG).show()
                onCreateClaimFailed()
                return
            }*/
            if (binding.edtTotalAmount.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please Enter Amount", Toast.LENGTH_LONG).show()

                onCreateClaimFailed()
                return
            }
            if (binding.edtTax.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please Enter Tax", Toast.LENGTH_LONG).show()

                onCreateClaimFailed()
                return
            }
            /*val fragment = SplitClaimFragmentEdit()
            fragment.setClaimRequestDetail(splitedClaimDetails)
            fragment.setClaimRequestDetailJson(newClaimRequest)
            fragment.setEditModeRM(true)
            fragment.setdropdownResponse(dropDownResponse)
            fragment.setCurrency(currencyCode,currencySymbol)*/

            val fragment = SplitClaimFragmentEdit()
            fragment.setClaimRequestDetail(getClaimRequest())
            fragment.setClaimRequestDetail(splitedClaimDetails)
            fragment.setClaimRequestDetailJson(newClaimRequest)
            fragment.setGroupName(groupname)
            fragment.setEditModeRM(true)
            fragment.setdropdownResponse(dropDownResponse)
            fragment.setCurrency(currencyCode,currencySymbol)


            (contextActivity as? MainActivity)?.addFragment(fragment)
          /*  if(viewModel.attachmentsList.size > 0){
                val fragment = EditSplitClaimDetailsFragment()
                fragment.setClaimRequestDetail(splitedClaimDetails)
                fragment.setClaimRequestDetailJson(newClaimRequest)
                fragment.setdropdownResponse(dropDownResponse)
                fragment.setCurrency(currencyCode,currencySymbol)
                (contextActivity as? MainActivity)?.addFragment(fragment)
            } else{
                Toast.makeText(contextActivity, "Please select receipt image to upload", Toast.LENGTH_LONG).show()
                return
            }*/

        }
        catch (e: Exception){
            Log.e(TAG, "createNewClaim: ${e.message}")
        }
    }

    private fun getEditClaimRequest() : JsonObject{
//     attachments = viewModel.attachmentsList.joinToString { it }

        val data= JsonObject()

        var dateFormate = if(companyDateFormate=="USA") {
            Constants.MMM_DD_YYYY_FORMAT
        }else{
            Constants.DD_MM_YYYY_FORMAT

        }


        data.addProperty("main_id",claimDetail.id)
        data.addProperty("split_id",splitedClaimDetails.main_claim?.split_id)
        data.addProperty("overall_status_id",splitedClaimDetails.main_claim?.overall_status_id)
        data.addProperty("fm_status_id",splitedClaimDetails.main_claim?.fm_status_id)
        data.addProperty("rm_status_id",splitedClaimDetails.main_claim?.rm_status_id)
        data.addProperty("title",binding.edtTitle.text.toString().trim())
        data.addProperty("merchantName",binding.edtMerchantName.text.toString().trim())
        if(binding.edtGroupValue.text.isEmpty())
        {
            data.addProperty("expenseGroup","")
        }else{
            data.addProperty("expenseGroup",if (!viewModel.expenseGroupList.isNullOrEmpty()) viewModel.expenseGroupList[binding.spnExpenseGroup.selectedItemPosition].id else "")

        }
        if(binding.edtExpenceTypeValue.text.isEmpty())
        {
            data.addProperty("expenseType","")
        }else{
            data.addProperty("expenseType",if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else "")

        }
        data.addProperty("companyNumber",if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyNumber.selectedItemPosition].id else "")
        data.addProperty("department", if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "")
        data.addProperty("dateSubmitted", Utils.getDateInServerRequestFormat(
            binding.tvDateOfSubmission.text.toString().trim(),
            dateFormate
        ))
        data.addProperty("currency", if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "")
        data.addProperty("totalAmount", binding.edtTotalAmount.text.toString())
        data.addProperty("tax", binding.edtTax.text.toString())
        data.addProperty("taxCode", if (!taxcodeId.isNullOrEmpty()) taxcodeId else "")
        data.addProperty("netAmount", binding.tvNetAmount.text.toString())
        data.addProperty("description", binding.edtDescription.text.toString().trim())
        data.addProperty("auction", binding.edtAutionValue.text.toString().trim())
        data.addProperty("expenseCode", if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "")
        val jsonArraySplitId= JsonArray()
        val jsonArraySplitData= JsonArray()
        splitedClaimDetails.splitedClaim?.forEachIndexed { index, splitClaimItem ->
            jsonArraySplitId.add(splitClaimItem.split_id)
            val data= JsonObject()
            var expenceCode= ""
            var mtaxcodeId= ""
            viewModel.expenseTypeList.forEach {
                if(it.id==splitClaimItem.expenseTypeID)
                    expenceCode=it.expenseCodeID
                mtaxcodeId=it.taxCodeID
            }
            var companyId= ""
            viewModel.companyList.forEach {
                if(it.code==splitClaimItem.companyNumber)
                    companyId=it.id
            }
            var departmentId= ""
            viewModel.departmentList.forEach {
                System.out.println("departmentid"+it.cost_code+" "+splitClaimItem.department)
                if(it.cost_code==splitClaimItem.department)
                    departmentId=it.id
            }
            data.addProperty("expense_type_id",splitClaimItem.expenseTypeID  )
            data.addProperty("company_id", companyId)
            data.addProperty("department_id", departmentId )
            data.addProperty("amount", splitClaimItem.totalAmount)
            data.addProperty("tax_code",mtaxcodeId)
            data.addProperty("auction",splitClaimItem.auction )
            data.addProperty("expense_code", expenceCode )
            jsonArraySplitData.add(data)
        }
        data.add("split_ids",jsonArraySplitId)
        data.add("split_data",jsonArraySplitData)


        return data
    }
    private fun getEditClaimRequestForSplit() : JsonObject{
//     attachments = viewModel.attachmentsList.joinToString { it }

        val data= JsonObject()

        var dateFormate = if(companyDateFormate=="USA") {
            Constants.MMM_DD_YYYY_FORMAT
        }else{
            Constants.DD_MM_YYYY_FORMAT

        }


        data.addProperty("main_id",claimDetail.id)
        data.addProperty("split_id",splitedClaimDetails.main_claim?.split_id)
        data.addProperty("overall_status_id",splitedClaimDetails.main_claim?.overall_status_id)
        data.addProperty("fm_status_id",splitedClaimDetails.main_claim?.fm_status_id)
        data.addProperty("rm_status_id",splitedClaimDetails.main_claim?.rm_status_id)
        data.addProperty("title",binding.edtTitle.text.toString().trim())
        data.addProperty("merchantName",binding.edtMerchantName.text.toString().trim())
        if(binding.edtGroupValue.text.isEmpty())
        {
            data.addProperty("expenseGroup","")
        }else{
            data.addProperty("expenseGroup",if (!viewModel.expenseGroupList.isNullOrEmpty()) viewModel.expenseGroupList[binding.spnExpenseGroup.selectedItemPosition].id else "")

        }
        if(binding.edtExpenceTypeValue.text.isEmpty())
        {
            data.addProperty("expenseType","")
        }else{
            data.addProperty("expenseType",if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else "")

        }
        data.addProperty("companyNumber",if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyNumber.selectedItemPosition].id else "")
        data.addProperty("department", if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "")
        data.addProperty("dateSubmitted", dateofRecipt)

        data.addProperty("currency", if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "")
        data.addProperty("totalAmount", binding.edtTotalAmount.text.toString())
        data.addProperty("tax", binding.edtTax.text.toString())
        data.addProperty("taxCode", if (!taxcodeId.isNullOrEmpty()) taxcodeId else "")
        data.addProperty("netAmount", binding.tvNetAmount.text.toString())
        data.addProperty("description", binding.edtDescription.text.toString().trim())
        data.addProperty("auction", binding.edtAutionValue.text.toString().trim())
        data.addProperty("expenseCode", if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "")
        return data
    }
    private fun validateCreateClaim(newClaimRequest: NewClaimRequest): Boolean {
        val isValid = viewModel.validateNewClaimRequest(newClaimRequest)
        if(!isValid.first){
            Toast.makeText(contextActivity, isValid.second, Toast.LENGTH_SHORT).show()
        }
        return isValid.first
    }

    private fun setResponse(commonResponse: CommonResponse) {
        binding.mProgressBars.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
        if(commonResponse.success) {
            shouldRefreshPage = true
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity(). finish()
//            (contextActivity as? MainActivity)?.backButtonPressed()
        //    (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }

    private fun onCreateClaimFailed() {
        binding.btnSubmit.isEnabled = true
    }

    private fun showCalenderDialog(){
        val calendar = Calendar.getInstance()
        val calendarStart: Calendar = Calendar.getInstance()
//        val calendarEnd: Calendar = Calendar.getInstance()

       // calendarStart.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH] - 1, calendar[Calendar.DAY_OF_MONTH])
//        calendarEnd.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        val constraintsBuilder =
            CalendarConstraints.Builder()
                //.setStart(calendarStart.timeInMillis)
                .setEnd(calendar.timeInMillis)
                .setValidator(DateValidatorPointBackward.now())


        val picker = MaterialDatePicker.Builder.datePicker()
            .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .setCalendarConstraints(constraintsBuilder.build())
            .build()
        activity?.supportFragmentManager?.let { picker.show(it, picker.toString()) }
        picker.addOnPositiveButtonClickListener {
            val date = Utils.getDateInDisplayFormatWithCountry(it,companyDateFormate)
            Log.d("DatePicker Activity", "Date String = ${date}:: Date epoch value = ${it}")
            dateofRecipt =Utils.getDateInDisplayFormatWithCountry2(it)

            binding.tvDateOfSubmission.text = date
        }
    }

    private fun showBottomSheet(){
        contextActivity?.let {
            val dialog = BottomSheetDialog(contextActivity!!, R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.item_bottom_sheet, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionOne = view.findViewById<TextView>(R.id.bottomOptionOne)
//            val dividerOne = view.findViewById<View>(R.id.dividerOne)
            val bottomOptionTwo = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val dividerTwo = view.findViewById<View>(R.id.dividerTwo)
            val bottomOptionThree = view.findViewById<TextView>(R.id.bottomOptionThree)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

            bottomOptionOne.text = resources.getString(R.string.upload_file)
            bottomOptionTwo.text = resources.getString(R.string.take_photo)
            dividerTwo.visibility = View.GONE
            bottomOptionThree.visibility = View.GONE

            bottomOptionOne.setOnClickListener {
                dialog.dismiss()
                choosePhotoFromGallery()
            }
            bottomOptionTwo.setOnClickListener {
                dialog.dismiss()
                takePhotoFromCamera()
            }
            bottomOptionCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun choosePhotoFromGallery() {
        contextActivity?. let {
            val intent = Lassi(contextActivity!!)
                .with(LassiOption.GALLERY) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
                .setCompressionRation(50) // compress image for single item selection (can be 0 to 100)
               // .setMinFileSize(50) // Restrict by minimum file size
               // .setMaxFileSize(100) //  Restrict by maximum file size
                //.disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
                .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 100)
        }
    }

    private fun takePhotoFromCamera(){
        contextActivity?. let {
            val intent = Lassi(contextActivity!!)
                .with(LassiOption.CAMERA) // choose Option CAMERA, GALLERY or CAMERA_AND_GALLERY
                .setMaxCount(1)
                .setGridSize(3)
                .setMediaType(MediaType.IMAGE) // MediaType : VIDEO IMAGE, AUDIO OR DOC
                .setCompressionRation(50) // compress image for single item selection (can be 0 to 100)
                .setMinFileSize(50) // Restrict by minimum file size
                .setMaxFileSize(100) //  Restrict by maximum file size
                .disableCrop() // to remove crop from the single image selection (crop is enabled by default for single image)
                .setStatusBarColor(R.color.secondary)
                .setToolbarResourceColor(R.color.white)
                .setProgressBarColor(R.color.secondary)
                .setToolbarColor(R.color.secondary)
                .setPlaceHolder(R.drawable.ic_image_placeholder)
                .setErrorDrawable(R.drawable.ic_image_placeholder)
                .build()
            startActivityForResult(intent, 101)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                100 -> {
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d(TAG, "onActivityResult: ${selectedMedia.size}")
                    if(selectedMedia.size > 0) {
                        val file= File( selectedMedia[0].path!!)
                        val filePath: String = file.path
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        viewModel.claimImageList.add(bitmap)

                        viewModel.attachmentsList.add(selectedMedia[0].path!!)
                        refreshAttachments()
                        Log.d(TAG, "onActivityResult:  attachmentsList: ${viewModel.attachmentsList.size}")
                    }
                }
                101 -> {
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d(TAG, "onActivityResult: ${selectedMedia.size}")
                    if(selectedMedia.size > 0) {
                        val file= File( selectedMedia[0].path!!)
                        val filePath: String = file.path
                        val bitmap = BitmapFactory.decodeFile(filePath)
                        viewModel.claimImageList.add(bitmap)
                        viewModel.attachmentsList.add(selectedMedia[0].path!!)

                        refreshAttachments()
                        Log.d(TAG, "onActivityResult:  attachmentsList: ${viewModel.attachmentsList.size}")
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    fun getBytes(`is`: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (`is`.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }
    override fun onDestroy() {
        super.onDestroy()
        if(shouldRefreshPage && this::refreshPageListener.isInitialized){
            refreshPageListener.refreshPage()
        }
    }
    private fun showImagePopup(imageUrl:String) {
        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_popup_dialog)

        val image = dialog.findViewById(R.id.itemImage) as ImageView
        Glide.with(requireContext())
            .load(imageUrl)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.mountains)
                    .error(R.drawable.mountains)
            )
            .placeholder(R.drawable.mountains)
            .error(R.drawable.mountains)
            .into(image)


        dialog.show()
        val noBtn = dialog.findViewById(R.id.lnClose) as LinearLayout
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun getDetails() {
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", claimDetail.id.toInt())
        }
        viewModel.getDetails(jsonObject,claimDetail.id).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                splitedClaimDetails=response
                                if(splitedClaimDetails.splitedClaim?.isEmpty() == true){
                                    binding.btnSplit.visibility=View.GONE
                                }else{
                                    var splittotalamount: Double = 0.0
                                    splitedClaimDetails.splitedClaim?.forEach {
                                        splittotalamount +=it.totalAmount.toDouble()

                                    }
                                    /*  try {
                                          binding.tvSplitAmount.setText(claimDetail.currencySymbol+" "+splittotalamount)
                                      } catch (e: Exception) {
                                          binding.tvSplitAmount.setText(splittotalamount.toString())

                                      }*/


                                }
                                try {
                                  //  binding.tvNetAmount.setText(String.format("%.2f", splitedClaimDetails.main_claim?.netAmount?.toDouble()))
                                    //   binding.tvNetAmount.setText(claimDetail.currencySymbol+" "+splitedClaimDetails.main_claim?.netAmount)

                                } catch (e: Exception) {
                                 //   binding.tvNetAmount.setText(splitedClaimDetails.main_claim?.netAmount)
                                    // binding.tvNetAmount.setText(splitedClaimDetails.main_claim?.netAmount.toString())


                                }

                                //setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteClaim: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    override fun callback(action: String, data: Any, postion: Int) {
        if (action == "show") {
            showImagePopup(data as String)
        }
        if (action == "remove") {
           val size =data as Int
            if(size<=0){
                refreshAttachments()
            }
        }
    }
}