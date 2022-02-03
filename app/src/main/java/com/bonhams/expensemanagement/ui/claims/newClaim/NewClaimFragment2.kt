package com.bonhams.expensemanagement.ui.claims.newClaim

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
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
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.FragmentNewClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.lassi.common.utils.KeyUtils
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiOption
import com.lassi.domain.media.MediaType
import com.lassi.presentation.builder.Lassi
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class NewClaimFragment2() : Fragment() ,RecylerCallback{

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var claimDetail: ClaimDetail
    private lateinit var viewModel: NewClaimViewModel
    private lateinit var binding: FragmentNewClaimBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_claim, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setClickListeners()
        setupAttachmentRecyclerView();
        setDropdownDataObserver()
        setupView()
        setupTextWatcher()
        return view
    }

    fun setClaimDetails(detail: ClaimDetail?){
        detail?.let {
            claimDetail = it
        }
    }

    fun setRefreshPageListener(refreshListener: RefreshPageListener){
        refreshPageListener = refreshListener
    }

    private fun setupView(){
        try {
            if (this::claimDetail.isInitialized) {
                println("isCopyClaim total amount :"+claimDetail.totalAmount)

                /*  binding.edtTitle.setText(
                      claimDetail.title.replaceFirstChar(Char::uppercase) ?: claimDetail.title
                  )*/
                binding.edtMerchantName.setText(
                    claimDetail.merchant.replaceFirstChar(Char::uppercase) ?: claimDetail.merchant
                )
//            binding.edtCompanyNumber.setText(claimDetail.companyName)
                binding.tvDateOfSubmission.text = if (!claimDetail.createdOn.trim().isNullOrEmpty())
                    Utils.getFormattedDate(claimDetail.createdOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,companyDateFormate
                    ) else ""
                binding.edtTotalAmount.setText(claimDetail.totalAmount)
                binding.edtTax.setText(claimDetail.tax)
                // binding.tvNetAmount.text = claimDetail.netAmount
                binding.tvNetAmount.setText(claimDetail.netAmount)
                binding.edtDescription.setText(claimDetail.description)
                if (!claimDetail.attachments.isNullOrEmpty() && claimDetail.attachments.trim()
                        .isNotEmpty()
                ) {
                    viewModel.attachmentsList = mutableListOf(claimDetail.attachments)
                }

            }
            refreshAttachments()
        }
        catch (error: Exception){
            Log.e(TAG, "setupView: ${error.message}")
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity(),
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }

    private fun setClickListeners(){
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
        // Expense Group Adapter
        val expenseGroupAdapter = CustomSpinnerAdapter(
            requireContext(),
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
                  //  println("selected group ID :$groupid")
                   // println("selected expenseGroupID  :"+it.expenseGroupID )
                  //  println("selected companyID  :"+it.companyID )
                   // println("selected compnyId  :"+compnyId )

                    if(it.expenseGroupID == groupid&&(it.companyID==compnyId.toString()||it.companyID==null)){
                        viewModel.expenseTypeList.add(it)
                       // println("selected expenseTypeList Added :" )

                    }else if(it.companyID.isNullOrEmpty()){
                        //viewModel.expenseTypeList.add(it)

                    }
                }
                setupExpenceType()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }





        // Company List Adapter
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
                    if(it.id.toInt()==viewModel.companyList[position].currency_type_id){
                        val symbol=it.symbol
                        val code=it.code
                        currencyCode=code
                        currencySymbol=symbol
                        // binding.edtTotalAmount.text = null
                        // binding.edtTax.text = null
                        // binding.tvNetAmount.text = null
                        // binding.edtTotalAmount.clearFocus()
                        //binding.edtTax.clearFocus()
                        // binding.tvNetAmount.clearFocus()

                        //binding.edtTotalAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                        //binding.edtTotalAmount.setLocale(code)

                       // binding.edtTax.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                       // binding.edtTax.setLocale(code)

                       // binding.tvNetAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                       // binding.tvNetAmount.setLocale(code)
                        val currency: Currency? =
                            viewModel.currencyList.find { it.id.toInt() == viewModel.companyList[position].currency_type_id }
                        val currencyPos = viewModel.currencyList.indexOf(currency)
                        if (currencyPos >= 0) {
                            binding.spnCurrency.setSelection(currencyPos)
                        }
                    }
                }
                binding.edtTitle.setText(viewModel.companyList[position].code)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(requireContext(), "Nothing selected", Toast.LENGTH_SHORT).show();

            }
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }



       /* binding.spnCompanyNumber.addOnLayoutChangeListener(View.OnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
            val position=binding.spnCompanyNumber.selectedItemPosition
            compnyId=viewModel.companyList[position].id.toInt()

            println("selected company currency id :"+ viewModel.companyList[position].currency_type_id)
            viewModel.departmentList.clear()
            viewModel.departmentListCompany.forEach {
                println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                if(it.company_id == compnyId.toString()){
                    viewModel.departmentList.add(it)
                }
            }
            setupDeparmentType()
            viewModel.currencyList.forEach {
                if(it.id.toInt()==viewModel.companyList[position].currency_type_id){
                    val symbol=it.symbol
                    val code=it.code
                    currencyCode=code
                    currencySymbol=symbol
                   // binding.edtTotalAmount.text = null
                   // binding.edtTax.text = null
                   // binding.tvNetAmount.text = null
                   // binding.edtTotalAmount.clearFocus()
                    //binding.edtTax.clearFocus()
                   // binding.tvNetAmount.clearFocus()

                    binding.edtTotalAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                    binding.edtTotalAmount.setLocale(code)

                    binding.edtTax.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                    binding.edtTax.setLocale(code)

                    binding.tvNetAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                    binding.tvNetAmount.setLocale(code)
                    val currency: Currency? =
                        viewModel.currencyList.find { it.id.toInt() == viewModel.companyList[position].currency_type_id }
                    val currencyPos = viewModel.currencyList.indexOf(currency)
                    if (currencyPos >= 0) {
                        binding.spnCurrency.setSelection(currencyPos)
                    }
                }
            }
            binding.edtTitle.setText(viewModel.companyList[position].code)
        })*/
       /* binding.spnCompanyNumber.addOnLayoutChangeListener(View.OnLayoutChangeListener { view, i, i1, i2, i3, i4, i5, i6, i7 ->
           val position=binding.spnExpenseGroup.selectedItemPosition

        })*/



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

               // binding.edtTotalAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
               // binding.edtTotalAmount.setLocale(code)

              //  binding.edtTax.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
              //  binding.edtTax.setLocale(code)

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
                val expenseGroup: ExpenseGroup? =
                    viewModel.expenseGroupList.find { it.id == claimDetail.expenseGroupID }
                val expenseGroupPos = viewModel.expenseGroupList.indexOf(expenseGroup)//(currencyAdapter).getPosition(expenseGroup)
                if (expenseGroupPos >= 0) {
                    binding.spnExpenseGroup.setSelection(expenseGroupPos)
                }

                val expenseType: ExpenseType? =
                    viewModel.expenseTypeList.find { it.id == claimDetail.expenseTypeID }
                val expenseTypePos = viewModel.expenseTypeList.indexOf(expenseType)
                if (expenseTypePos >= 0) {
                    binding.spnExpenseType.setSelection(expenseTypePos)
                }

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
            }
            catch (e: Exception){
                Log.e(TAG, "setupSpinners: ${e.message}")
            }
        }
    }
    private fun setupExpenceType(){
        // Expense Type Adapter
        println("call expence type method ")
            val expenseTypeAdapter = CustomSpinnerAdapter(
                requireContext(),
                R.layout.item_spinner,
                viewModel.expenseTypeList
            )
            binding.spnExpenseType.adapter = expenseTypeAdapter
        binding.spnExpenseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                expenseCode=viewModel.expenseTypeList.get(position).activityCode
                mtaxcodeId= viewModel.expenseTypeList[position].taxCodeID

                setupTax()
                if(binding.edtAutionValue.text.toString().isNotEmpty()){
                    binding.tvAuctionExpCode.text = expenseCode

                }else{
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
                  //  updateNetAmount(binding.edtTotalAmount.text.toString(), binding.edtTax.getNumericValue().toString())

            }
        })

        binding.edtTax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   //updateNetAmount(binding.edtTotalAmount.text.toString(), binding.edtTax.getNumericValue().toString())
            }
        })
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
        if(viewModel.attachmentsList.size > 0){
            binding.tvNoFileSelected.visibility = View.GONE
            binding.rvAttachments.visibility = View.VISIBLE
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

                binding.tvNetAmount.setText("0.0")

            }else {
                val netAmount = totalAmount - taxAmount
                // binding.tvNetAmount.text = "$netAmount"
                binding.tvNetAmount.setText(String.format("%.2f", netAmount))
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
        viewModel.expenseGroupList = dropdownResponse.expenseGroup as MutableList<ExpenseGroup>
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

       /* val requestBody: RequestBody
        val body: MultipartBody.Part
        val mapRequestBody = LinkedHashMap<String, RequestBody>()
        val arrBody: MutableList<MultipartBody.Part> = ArrayList()
        val file=File( viewModel.attachmentsList.get(0))
        requestBody = RequestBody.create(okhttp3.MediaType.parse("multipart/form-data"), file)
        mapRequestBody["file\"; filename=\"" + file] = requestBody
        mapRequestBody["test"] =
            RequestBody.create(okhttp3.MediaType.parse("text/plain"), "gogogogogogogog")


        body = MultipartBody.Part.createFormData("claimImage", file.getName(), requestBody)
        arrBody.add(body)

        viewModel.uploadClaimAttachement(arrBody).observe(viewLifecycleOwner, Observer {
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
       viewModel.createNewClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
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
        })
    }

    private fun createNewClaim() {
        try {
            val newClaimRequest = getClaimRequest()

            if (!validateCreateClaim(newClaimRequest)) {
                onCreateClaimFailed()
                return
            }

            binding.btnSubmit.visibility = View.GONE
            setCreateClaimObserver(newClaimRequest)
        }
        catch (e: Exception){
            Log.e(TAG, "createNewClaim: ${e.message}")
        }
    }

    private fun splitNewClaim() {
        //  attachments = viewModel.attachmentsList.joinToString { it }
        try {
            val newClaimRequest = getClaimRequest()

            if (!validateCreateClaim(newClaimRequest)) {
                onCreateClaimFailed()
                return
            }

            val fragment = SplitClaimFragment()
            fragment.setClaimRequestDetail(newClaimRequest)
            fragment.setCurrency(currencyCode,currencySymbol)
            (contextActivity as? MainActivity)?.addFragment(fragment)
        }
        catch (e: Exception){
            Log.e(TAG, "createNewClaim: ${e.message}")
        }
    }

    private fun getClaimRequest() : NewClaimRequest{
//      attachments = viewModel.attachmentsList.joinToString { it }


        var dateFormate = if(companyDateFormate=="USA") {
            Constants.MMM_DD_YYYY_FORMAT
        }else{
            Constants.DD_MM_YYYY_FORMAT

        }
        return viewModel.getNewClaimRequest(
            binding.edtTitle.text.toString().trim(),
            binding.edtMerchantName.text.toString().trim(),
            if (!viewModel.expenseGroupList.isNullOrEmpty()) viewModel.expenseGroupList[binding.spnExpenseGroup.selectedItemPosition].id else "",
            if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else "",
            if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyNumber.selectedItemPosition].id else "",
//            binding.edtCompanyNumber.text.toString().trim(),
            if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "",
            Utils.getDateInServerRequestFormat(
                binding.tvDateOfSubmission.text.toString().trim(),
                dateFormate
            ),
            if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "",
            binding.edtTotalAmount.text.toString(),
            binding.edtTax.text.toString().trim(),
            binding.tvNetAmount.text.toString().trim(),
            binding.edtDescription.text.toString().trim(),
            if (!taxcodeId.isNullOrEmpty()) taxcodeId else "",
            binding.edtAutionValue.text.toString().trim(),
            if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "",

            viewModel.claimImageList as List<String>,
            viewModel.attachmentsList as List<String>
        )
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

        calendarStart.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH] - 1, calendar[Calendar.DAY_OF_MONTH])
//        calendarEnd.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(calendarStart.timeInMillis)
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
                        viewModel.attachmentsList.add(selectedMedia[0].path!!)
                        refreshAttachments()
                        Log.d(TAG, "onActivityResult:  attachmentsList: ${viewModel.attachmentsList.size}")
                    }
                }
                101 -> {
                    val selectedMedia = data.getSerializableExtra(KeyUtils.SELECTED_MEDIA) as ArrayList<MiMedia>
                    Log.d(TAG, "onActivityResult: ${selectedMedia.size}")
                    if(selectedMedia.size > 0) {
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

    override fun callback(action: String, data: Any, postion: Int) {
    }
}