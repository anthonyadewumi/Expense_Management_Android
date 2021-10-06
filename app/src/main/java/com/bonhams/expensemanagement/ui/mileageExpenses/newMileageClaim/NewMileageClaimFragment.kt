package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.bonhams.expensemanagement.data.services.GoogleApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.FragmentNewMileageClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
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
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*


class NewMileageClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentNewMileageClaimBinding
    private lateinit var viewModel: NewMileageClaimViewModel

    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private lateinit var mileageDetail: MileageDetail
    private lateinit var fromResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var toResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var refreshPageListener: RefreshPageListener

    private var shouldRefreshPage: Boolean = false
    private var fromadd: String = ""
    private var toadd: String = ""
    private var expenseCode: String = ""
    private var taxcodeId: String = ""
    private var milageRate:Double=4.0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_mileage_claim, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setClickListeners()
        setupAttachmentRecyclerView()
        setDropdownDataObserver()
        setupView()
        setupTextWatcher()
        setupAutoCompletePlaces()

        return view
    }

    fun setMileageDetails(detail: MileageDetail?){
        detail?.let {
            mileageDetail = it
        }
    }

    fun setRefreshPageListener(refreshListener: RefreshPageListener){
        refreshPageListener = refreshListener
    }
    private fun setupView(){
        try {
            if (this::mileageDetail.isInitialized) {
             //   binding.edtTitle.setText(mileageDetail.title)
                binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                    mileageDetail.submittedOn,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
                )
                binding.edtMerchantName.setText(mileageDetail.merchant)
                binding.tvDateOfTrip.text = Utils.getFormattedDate(
                    mileageDetail.tripDate,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
                )
                binding.tvTripFrom.text = mileageDetail.fromLocation
                binding.tvTripTo.text = mileageDetail.toLocation
                binding.edtClaimedMiles.setText(mileageDetail.claimedMileage)
                binding.edtPetrolAmount.setText(mileageDetail.petrolAmount)
                binding.edtParkAmount.setText(mileageDetail.parking)
                binding.edtTotalAmount.setText(mileageDetail.totalAmount)
                binding.edtTax.setText(mileageDetail.tax)
                binding.tvNetAmount.text = mileageDetail.netAmount
                binding.edtDescription.setText(mileageDetail.description)


                binding.switchRoundTrip.isChecked = mileageDetail.isRoundTrip == "1"
                if (!mileageDetail.attachments.isNullOrEmpty() && mileageDetail.attachments.trim()
                        .isNotEmpty()
                ) {
                    viewModel.attachmentsList = mutableListOf(mileageDetail.attachments)
                }
            }

            refreshAttachments()
        }
        catch (error: Exception){
            Log.e(TAG, "setupView: ${error.message}")
        }
    }

    private fun setClickListeners(){
        binding.tvUploadPic.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })
        binding.ivPicUpload.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })

        binding.tvDateOfSubmission.setOnClickListener(View.OnClickListener {
            showCalenderDialog(true)
        })

        binding.tvDateOfTrip.setOnClickListener(View.OnClickListener {
            showCalenderDialog(false)
        })

        binding.tvTripFrom.setOnClickListener(View.OnClickListener {
            openAutoCompletePlaces(true)
        })

        binding.tvTripTo.setOnClickListener(View.OnClickListener {
            openAutoCompletePlaces(false)
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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewMileageClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService),
                GoogleApiHelper(RetrofitBuilder.googleApiService)
            )
        ).get(NewMileageClaimViewModel::class.java)
    }

    private fun showLogoutAlert() {
        val dialog = context?.let { Dialog(it) }
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_force_logout_alert_dialog)
        val title = dialog?.findViewById(R.id.txtTitle) as TextView
        val body = dialog.findViewById(R.id.txtDescription) as TextView
        val input = dialog.findViewById(R.id.edtDescription) as EditText
        //val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
        val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

        input.visibility = View.GONE
        title.text = resources.getString(R.string.logout)
        body.text = resources.getString(R.string.are_you_sure_you_want_to_logout)
        //yesBtn.text = resources.getString(R.string.logout)
        noBtn.text = resources.getString(R.string.ok)


        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog?.show()
    }



    private fun setupSpinners(){
        // Company List Adapter
        val companyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.companyList
        )
        binding.spnCompanyName.adapter = companyAdapter
        var compnypostion=0
        viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.company.equals(element.name)){
                compnypostion=index
                return@forEachIndexed
            }
        }
        binding.spnCompanyName.setSelection(compnypostion)
        binding.spnCompanyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.edtTitle.setText(viewModel.companyList[position].code)

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
        // Department Adapter
        val mileageTypeAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.mileageTypeList
        )
        binding.spnMileageType.adapter = mileageTypeAdapter
        binding.spnMileageType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                val item = viewModel.mileageTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        val departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
        var dpostion=0
        viewModel.departmentList.forEachIndexed { index, element ->

            if(AppPreferences.department.equals(element.name)){
                dpostion=index
                return@forEachIndexed
            }
        }
        binding.spnDepartment.setSelection(dpostion)
        binding.spnDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                val item = viewModel.departmentList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Type Adapter
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
                taxcodeId=viewModel.expenseTypeList.get(position).taxCodeID
                viewModel.taxList.forEach {
                    if(it.id.toString().equals(taxcodeId)) {
                        binding.edtTaxcode.setText(it.tax_code)

                    }
                }

                if(!binding.edtAutionValue.text.toString().isEmpty()){
                    binding.tvAuctionExpCode.text = expenseCode

                }else{
                    binding.tvAuctionExpCode.text = ""
                }
//                val item = viewModel.expenseTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Distance Adapter
        val distanceAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.distanceList
        )
        binding.spnDistance.adapter = distanceAdapter
        binding.spnDistance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                val item = viewModel.distanceList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Tax Adapter
        /*val taxAdapter = CustomSpinnerAdapter(
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
        }*/

        // Car Type Adapter
        val carTypeAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.carTypeList
        )
        binding.spnCarType.adapter = carTypeAdapter
        binding.spnCarType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
//                val item = viewModel.carTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
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
//                val item = viewModel.currencyList[position].name
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        if(this::mileageDetail.isInitialized){
            try {
                val company: Company? =
                    viewModel.companyList.find { it.name == mileageDetail.companyName }
                val companyPos = viewModel.companyList.indexOf(company)
                if (companyPos >= 0) {
                    binding.spnCompanyName.setSelection(companyPos)
                }

                val mileageType: MileageType? =
                    viewModel.mileageTypeList.find { it.type == mileageDetail.type }
                val mileageTypePos = viewModel.mileageTypeList.indexOf(mileageType)
                if (mileageTypePos >= 0) {
                    binding.spnCompanyName.setSelection(mileageTypePos)
                }

                val department: Department? =
                    viewModel.departmentList.find { it.name == mileageDetail.department }
                val departmentPos = viewModel.departmentList.indexOf(department)
                if (departmentPos >= 0) {
                    binding.spnDepartment.setSelection(departmentPos)
                }

                val expenseType: ExpenseType? =
                    viewModel.expenseTypeList.find { it.name == mileageDetail.type }
                val expenseTypePos = viewModel.expenseTypeList.indexOf(expenseType)
                if (expenseTypePos >= 0) {
                    binding.spnExpenseType.setSelection(expenseTypePos)
                }

                val expenseGroup: ExpenseGroup? =
                    viewModel.distanceList.find { it.name == mileageDetail.distance }
                val expenseGroupPos = viewModel.distanceList.indexOf(expenseGroup)
                if (expenseGroupPos >= 0) {
                    binding.spnDistance.setSelection(expenseGroupPos)
                }

                val carType: CarType? =
                    viewModel.carTypeList.find { it.type == mileageDetail.carType }
                val carTypePos = viewModel.carTypeList.indexOf(carType)
                if (carTypePos >= 0) {
                    binding.spnCarType.setSelection(carTypePos)
                }

                val currency: Currency? =
                    viewModel.currencyList.find { it.id == mileageDetail.currencyID }
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

    private fun setupTextWatcher(){
        binding.edtTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNetAmount(s.toString(), binding.edtTax.text.toString())
            }
        })

        binding.edtTax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateNetAmount(binding.edtTotalAmount.text.toString(), s.toString())
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

    private fun setupAutoCompletePlaces(){
        contextActivity?.let {
            // Fetching API_KEY which we wrapped
            val ai: ApplicationInfo = it.packageManager.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
            val value = ai.metaData["AIzaSyBG514Hl7ekIEU3iyXKcnqBi0vvIgjtp-8"]
           // val apiKey = value.toString()
            val apiKey = "AIzaSyBG514Hl7ekIEU3iyXKcnqBi0vvIgjtp-8"
            // Initializing the Places API
            // with the help of our API_KEY
            if (!Places.isInitialized()) {
                Places.initialize(it, apiKey)
            }
        }

        fromResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        Log.d(TAG, "setupAutoCompletePlaces: Place: ${place.name}, ${place.id}")
                        binding.tvTripFrom.text = place.name
                        fromadd= place.name.toString()
                        if(binding.tvTripFrom.text.isNotEmpty()&&binding.tvTripTo.text.isNotEmpty())
                        getmatrixDistanceObserver(fromadd,toadd)

                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    result.data?.let {
                        val status = Autocomplete.getStatusFromIntent(it)
                        Log.e(TAG, "setupAutoCompletePlaces: ${status.statusMessage}")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                    Log.d(TAG, "setupAutoCompletePlaces: RESULT_CANCELED")
                }
            }
        }

        toResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    result.data?.let {
                        val place = Autocomplete.getPlaceFromIntent(it)
                        Log.d(TAG, "setupAutoCompletePlaces: Place: ${place.name}, ${place.id}")
                        binding.tvTripTo.text = place.name
                        toadd= place.name.toString()
                        if(binding.tvTripFrom.text.isNotEmpty()&&binding.tvTripTo.text.isNotEmpty())
                            getmatrixDistanceObserver(fromadd,toadd)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    result.data?.let {
                        val status = Autocomplete.getStatusFromIntent(it)
                        Log.e(TAG, "setupAutoCompletePlaces: ${status.statusMessage}")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                    Log.d(TAG, "setupAutoCompletePlaces: RESULT_CANCELED")
                }
            }
        }
    }

    private fun openAutoCompletePlaces(isFromLocation: Boolean){
        contextActivity?.let {
            val fields = listOf(Place.Field.ID, Place.Field.NAME)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(it)
            if(isFromLocation)
                fromResultLauncher.launch(intent)
            else
                toResultLauncher.launch(intent)
        }
    }

    private fun setupAttachmentRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttachments.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"milage")
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun createNewClaim() {

        try {
            val claimRequest = viewModel.getNewMileageClaimRequest(
                binding.edtTitle.text.toString().trim(),
                if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyName.selectedItemPosition].id else "",
                if (!viewModel.mileageTypeList.isNullOrEmpty()) viewModel.mileageTypeList[binding.spnMileageType.selectedItemPosition].id else "",
                if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "",
                Utils.getDateInServerRequestFormat(
                    binding.tvDateOfSubmission.text.toString().trim(),
                    Constants.DD_MMM_YYYY_FORMAT
                ),
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else "",
                binding.edtMerchantName.text.toString().trim(),
                Utils.getDateInServerRequestFormat(
                    binding.tvDateOfTrip.text.toString().trim(),
                    Constants.DD_MMM_YYYY_FORMAT
                ),
                binding.tvTripFrom.text.toString().trim(),
                binding.tvTripTo.text.toString().trim(),
                "1"/*binding.spnDistance*/,
                if (!viewModel.carTypeList.isNullOrEmpty()) viewModel.carTypeList[binding.spnCarType.selectedItemPosition].id else "", //spnCurrency.selectedItemPosition,
                binding.edtClaimedMiles.text.toString().trim(),
                binding.switchRoundTrip.isChecked,
                if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "",
                binding.edtPetrolAmount.text.toString().trim(),
                binding.edtParkAmount.text.toString().trim(),
                binding.edtTotalAmount.text.toString().trim(),
                binding.edtTax.text.toString().trim(),
                binding.tvNetAmount.text.toString().trim(),
                binding.edtDescription.text.toString().trim(),
                if (!taxcodeId.isNullOrEmpty()) taxcodeId else "",
                binding.edtAutionValue.text.toString().trim(),
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "",

                viewModel.attachmentsList as List<String>
            )

            if (!validateCreateClaim(claimRequest)) {
                onCreateClaimFailed()
                return
            }

            binding.btnSubmit.visibility = View.GONE
            setCreateClaimObserver(claimRequest)
        }
        catch (error: Exception){
            Log.e(TAG, "createNewClaim: ${error.message}")
        }
    }
    private fun getmatrixDistanceObserver(origins:String,destinations:String) {
         viewModel.getDistance(origins,destinations).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            val distance=
                                response.rows[0].elements.get(0).distance?.value?.let { it1 ->
                                    meterToKiloMeter(
                                        it1
                                    )
                                }
                            val fare= distance?.times(milageRate)
                            val decimal =
                                fare?.let { it1 -> BigDecimal(it1).setScale(2, RoundingMode.HALF_EVEN) }
                            binding.edtClaimedMiles.setText(distance.toString())
                            binding.edtTotalAmount.setText(decimal.toString())
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                  binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setDropdownDataObserver() {
        viewModel.getDropDownData().observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setDropdownDataObserver: ${resource.status}")
                                initializeSpinnerData(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "setDropdownDataObserver: ${it.message}")
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
        if(dropdownResponse.message.equals("Invalid token"))
        {
            showLogoutAlert()
        }else {
            viewModel.companyList = dropdownResponse.companyList
            viewModel.departmentList = dropdownResponse.departmentList
            viewModel.expenseTypeList = dropdownResponse.expenseType
            viewModel.distanceList = dropdownResponse.expenseGroup
            viewModel.carTypeList = dropdownResponse.carType
            viewModel.currencyList = dropdownResponse.currencyType
            viewModel.mileageTypeList = dropdownResponse.mileageType
            viewModel.taxList = dropdownResponse.tax

            setupSpinners()
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

            val netAmount = totalAmount - taxAmount
            binding.tvNetAmount.text = "$netAmount"
        }
        catch (error: Exception){
            Log.e(TAG, "updateNetAmount: ${error.message}")
        }
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

    private fun setCreateClaimObserver(mileageClaimRequest: NewMileageClaimRequest) {
        viewModel.createNewMileageClaim(mileageClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "setChangePasswordObserver: ${resource.status}")
                               // Toast.makeText(contextActivity, "Mileage Claim added successfully/submitted successfully", Toast.LENGTH_SHORT).show()

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

    private fun validateCreateClaim(newClaimRequest: NewMileageClaimRequest): Boolean {
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
//            (contextActivity as? MainActivity)?.backButtonPressed()
            (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }

    private fun onCreateClaimFailed() {
        binding.btnSubmit.isEnabled = true
    }

    private fun showCalenderDialog(isDateOfSubmission: Boolean){
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
            val date = Utils.getDateInDisplayFormat(it)
            Log.d("DatePicker Activity", "Date String = ${date}:: Date epoch value = ${it}")
            if(isDateOfSubmission)
                binding.tvDateOfSubmission.text = date
            else
                binding.tvDateOfTrip.text = date
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
                .setCompressionRation(20) // compress image for single item selection (can be 0 to 100)
                .setMinFileSize(100) // Restrict by minimum file size
                .setMaxFileSize(1024) //  Restrict by maximum file size
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
                .setCompressionRation(20) // compress image for single item selection (can be 0 to 100)
                .setMinFileSize(100) // Restrict by minimum file size
                .setMaxFileSize(1024) //  Restrict by maximum file size
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
    private fun meterToMiles(meter:Int):Double{
       val CONVERSION_UNIT = 0.00062137119;
        return meter * CONVERSION_UNIT
    }
    private fun meterToKiloMeter(meter:Int):Double{
        return meter * 0.001
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                99 -> {
                    data.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    }
                    Log.d(TAG, "onActivityResult: ")
                }
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

    override fun onDestroy() {
        super.onDestroy()
        if(shouldRefreshPage && this::refreshPageListener.isInitialized){
            refreshPageListener.refreshPage()
        }
    }
}