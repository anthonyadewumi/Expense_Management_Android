package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
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
import com.bonhams.expensemanagement.data.services.requests.EditMileageClaimRequest
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.FragmentNewMileageClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale


class EditMileageClaimFragment : Fragment() ,RecylerCallback{

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentNewMileageClaimBinding
    private lateinit var viewModel: NewMileageClaimViewModel

    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private lateinit var mileageDetail: MileageDetail
    private lateinit var gpsMileageDetail: GpsMileageDetail
    private lateinit var fromResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var toResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var refreshPageListener: RefreshPageListener

    private var shouldRefreshPage: Boolean = false
    private var fromadd: String = ""
    private var toadd: String = ""
    private var expenseCode: String = ""
    private var mtaxcodeId: String = ""
    private var taxcodeId: String = ""
    private var expenseCodeId: String = ""
    private var milageRate:Double=0.00
    private var compnyId: Int = 0
    private var currencyCode: String = ""
    private var distanceType: String = ""
    private var currencySymbol: String = ""
    private var countryName="United States"
    private var companyDateFormate: String = ""
    private var companyLocation: String = ""
    private var submitDate: String = ""
    private var tripDate: String = ""
    private var icCreateCopy: Boolean = false
    private var isennitial: Boolean = true
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
    fun setAutoMileageDetails(detail: GpsMileageDetail?){
        detail?.let {
            gpsMileageDetail=it

        }
    }

    fun setRefreshPageListener(refreshListener: RefreshPageListener){
        refreshPageListener = refreshListener
    }
    private fun setupView(){
        try {
            binding.txtAttachement.visibility=View.GONE
            binding.rlAttachement.visibility=View.GONE
            binding.tvCTotalClaimedMiles.text = AppPreferences.claimedMils

            var dateFormate = if(companyDateFormate=="USA") {
                Constants.MMM_DD_YYYY_FORMAT
            }else{
                Constants.DD_MM_YYYY_FORMAT

            }
            val sdf = SimpleDateFormat(dateFormate)
            binding.tvotalClaimedDate.text = sdf.format(Calendar.getInstance().time)
            if (this::mileageDetail.isInitialized) {
                icCreateCopy=true
             //   binding.edtTitle.setText(mileageDetail.title)
               /* binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                    mileageDetail.submittedOn,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,companyDateFormate
                )*/

                binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                    mileageDetail.submittedOn,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                )

                submitDate = Utils.getFormattedDate2(
                    mileageDetail.submittedOn,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
                )



                binding.edtMerchantName.setText(mileageDetail.merchant)
                binding.tvDateOfTrip.text = Utils.getFormattedDate(
                    mileageDetail.tripDate,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,companyDateFormate
                )
                tripDate = Utils.getFormattedDate2(
                    mileageDetail.tripDate,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
                )
                binding.tvTripFrom.text = mileageDetail.fromLocation
                binding.tvTripTo.text = mileageDetail.toLocation
                binding.edtClaimedMiles.setText(mileageDetail.distance)
                binding.edtClaimedMiles2.setText(mileageDetail.claimedMileage)
                binding.edtPetrolAmount.setText(String.format("%.2f",mileageDetail.petrolAmount.toDouble()))
                binding.edtParkAmount.setText(String.format("%.2f",mileageDetail.parking.toDouble()))
                binding.edtTotalAmount.setText(String.format("%.2f",mileageDetail.totalAmount.toDouble()))
                binding.edtTax.setText(mileageDetail.mtax.toString())
                //binding.tvNetAmount.text = mileageDetail.netAmount
                binding.tvNetAmount.setText(String.format("%.2f",mileageDetail.netAmount.toDouble()))
                binding.edtDescription.setText(mileageDetail.description)
                binding.tvAuctionExpCode.setText(mileageDetail.expenseCode)
                binding.edtAutionValue.setText(mileageDetail.auction)


                binding.switchRoundTrip.isChecked = mileageDetail.isRoundTrip == "1"

               /* if (!mileageDetail.attachments.isNullOrEmpty() && mileageDetail.attachments.trim()
                        .isNotEmpty()
                ) {
                    viewModel.attachmentsList = mutableListOf(mileageDetail.attachments)
                }*/
            }

           // refreshAttachments()
        }
        catch (error: Exception){
            Log.e(TAG, "setupView: ${error.message}")
        }
    }

    private fun setClickListeners(){
        binding.tvUploadPic.setOnClickListener {
            showBottomSheet()
        }
        binding.ivPicUpload.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })

        binding.tvDateOfSubmission.setOnClickListener(View.OnClickListener {
            showCalenderDialog(true)
        })

        binding.tvDateOfTrip.setOnClickListener(View.OnClickListener {
            showCalenderDialog(false)
        })

        binding.tvTripFrom.setOnClickListener {
            openAutoCompletePlaces(true)
        }

        binding.tvTripTo.setOnClickListener(View.OnClickListener { openAutoCompletePlaces(false) })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                if(NoInternetUtils.isConnectedToInternet(it))
                    createNewClaim()
                else
                    Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
        })
        binding.rlDefault.setOnClickListener {
            if(binding.lnDefaultContent.isVisible){
                binding.lnDefaultContent.visibility=View.GONE
                binding.ivDefaultDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnDefaultContent.visibility=View.VISIBLE
                binding.ivDefaultDropDown.setImageResource(R.drawable.drop_down)
            }
        }
        binding.rlDetails.setOnClickListener {
            if(binding.lnDetailsContant.isVisible){
                binding.lnDetailsContant.visibility=View.GONE
                binding.ivDetailsDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnDetailsContant.visibility=View.VISIBLE
                binding.ivDetailsDropDown.setImageResource(R.drawable.drop_down)
            }
        }
        binding.rlMilegae.setOnClickListener {
            if(binding.lnMileageContant.isVisible){
                binding.lnMileageContant.visibility=View.GONE
                binding.ivMeliageDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnMileageContant.visibility=View.VISIBLE
                binding.ivMeliageDropDown.setImageResource(R.drawable.drop_down)
            }
        }
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
        dialog.show()
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
        if (this::mileageDetail.isInitialized) {
             viewModel.companyList.forEachIndexed { index, element ->

            if(mileageDetail.companyName == element.name){
                compnypostion=index
                return@forEachIndexed
            }
        }

        }else{
             viewModel.companyList.forEachIndexed { index, element ->

            if(AppPreferences.company == element.name){
                compnypostion=index
                return@forEachIndexed
            }
        }
        }

        binding.spnCompanyName.setSelection(compnypostion)
        binding.spnCompanyName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                binding.edtTitle.setText(viewModel.companyList[position].code)
                milageRate=  getMileageRate(viewModel.companyList[position].id.toInt())
                if(milageRate>0)
                {
                    binding.edtMileageRate.setText(milageRate.toString())

                }else{
                    binding.edtMileageRate.setText("")

                    showAleartDialog("Mileage Rate is not available for Company No "+viewModel.companyList[position].code)

                }

                compnyId=viewModel.companyList[position].id.toInt()
                companyDateFormate=viewModel.companyList[position].dateFormat
                companyLocation=viewModel.companyList[position].location

                if(companyLocation=="UNITED STATE"){
                    binding.spnMileageType.setSelection(0)
                }else{
                    binding.spnMileageType.setSelection(1)
                }

                println("selected company currency id :$companyLocation")
                viewModel.departmentList.clear()
                viewModel.departmentListCompany.forEach {
                    println("selected department compnyid id :"+ it.company_id+" and company id$"+compnyId)

                    if(it.company_id == compnyId.toString()){
                        viewModel.departmentList.add(it)
                    }
                }
                binding.spnExpenseGroup.adapter=null
                setupExpenceGroup()
                setupDeparmentType()

                viewModel.currencyList.forEach {
                    if(it.id.toInt()==viewModel.companyList[position].currency_type_id){
                        val symbol=it.symbol
                        val code=it.code
                        currencyCode=code
                        currencySymbol=symbol
                        //binding.edtTax.setText("0")
                        //binding.edtTotalAmount.setText("")
                       // binding.edtPetrolAmount.setText("")
                       // binding.edtParkAmount.setText("")
                       // binding.tvNetAmount.setText("")
                       // binding.tvDateOfSubmission.text = ""
                      //  binding.tvDateOfTrip.text = ""
                       // binding.edtClaimedMiles2.setText("")
                        binding.edtTax.addDecimalLimiter()
                        binding.edtPetrolAmount.addDecimalLimiter()
                        binding.edtParkAmount.addDecimalLimiter()
                        binding.tvNetAmount.addDecimalLimiter()

                        binding.tvTaxCurrency.text = symbol



                       // binding.tvNetAmount.setCurrencySymbol(symbol, useCurrencySymbolAsHint = true)
                        //binding.tvNetAmount.setLocale(code)

                        if(icCreateCopy){
                             var postion=0
         viewModel.currencyList.forEachIndexed { index, element ->
             if(mileageDetail.currencyID == element.id){
                 postion=index
                 return@forEachIndexed
             }
         }
         binding.spnCurrency.setSelection(postion)
                        }else {

                            val currency: Currency? =
                                viewModel.currencyList.find { it.id.toInt() == viewModel.companyList[position].currency_type_id }
                            val currencyPos = viewModel.currencyList.indexOf(currency)
                            if (currencyPos >= 0) {
                                binding.spnCurrency.setSelection(currencyPos)
                            }
                        }

                    }
                }

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
                val type=viewModel.mileageTypeList[position].type
                if(type=="KM"){
                    binding.txtMilesKm.text = "Calculated KM*"
                    binding.txtMilesKmManuaaly.text = "Claimed KM*"
                    binding.edtClaimedMiles.hint = " Calculated KM"
                    binding.edtClaimedMiles2.hint = "Claimed KM"
                    distanceType="KM"
                }else{
                    binding.txtMilesKm.text = "Calculated Miles*"
                    binding.txtMilesKmManuaaly.text = "Claimed Miles*"
                    binding.edtClaimedMiles.hint = "Calculated Miles"
                    binding.edtClaimedMiles2.hint = "Claimed Miles"
                    distanceType="Miles"
                }

//                val item = viewModel.mileageTypeList[position]
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

        // Car Type Adapter
       /* val carTypeAdapter = CustomSpinnerAdapter(
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
*/
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

                val code = viewModel.currencyList[position].code
                val symbol = viewModel.currencyList[position].symbol
                currencyCode=code
                currencySymbol=symbol
                binding.tvTaxCurrency.text = symbol
                binding.tvTotalAmountCurrency.text = symbol
                binding.tvPetrolAmountCurrency.text = symbol
                binding.tvParkAmountCurrency.text = symbol
                binding.tvNetAmountCurrency.text = symbol
                if(icCreateCopy){

                    binding.edtClaimedMiles.setText(mileageDetail.claimedMileage)
                    binding.edtPetrolAmount.setText(mileageDetail.petrolAmount)
                    binding.edtParkAmount.setText(mileageDetail.parking)
                    binding.edtTotalAmount.setText(mileageDetail.totalAmount)
                    binding.edtTax.setText(mileageDetail.mtax.toString())
                    //binding.tvNetAmount.text = mileageDetail.netAmount
                    binding.tvNetAmount.setText(mileageDetail.netAmount)
                    binding.edtDescription.setText(mileageDetail.description)
                }


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
                    binding.spnMileageType.setSelection(mileageTypePos)
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
                binding.edtCarType.setText(viewModel.carTypeList.get(carTypePos).type)

               /* if (carTypePos >= 0) {
                    binding.spnCarType.setSelection(carTypePos)
                }*/

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
            refreshAttachments()

        }else{
            binding.edtMerchantName.setText(AppPreferences.ledgerId)

        }
        if(this::gpsMileageDetail.isInitialized){
            binding.tvDateOfTrip.text = gpsMileageDetail.startDate
            binding.tvDateOfSubmission.text = gpsMileageDetail.stopDate
            binding.tvTripFrom.text = gpsMileageDetail.fromLocation
            binding.tvTripTo.text = gpsMileageDetail.toLoaction
            getmatrixDistanceObserver(gpsMileageDetail.fromLocation,gpsMileageDetail.toLoaction)
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
    private fun setupTextWatcher(){
        binding.edtTotalAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtTotalAmount.text.isNotEmpty())
                updateNetAmount(binding.edtTotalAmount.text.toString().toDouble(), binding.edtTax.text.toString().toDouble())
            }
        })
        binding.edtClaimedMiles2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtClaimedMiles2.text.isNotEmpty()&&binding.edtMileageRate.text.isNotEmpty())
                updateTotalAmount(binding.edtClaimedMiles2.text.toString().toDouble(),binding.edtMileageRate.text.toString().toDouble())
            }
        })
        binding.edtMileageRate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtClaimedMiles2.text.isNotEmpty()&&binding.edtMileageRate.text.isNotEmpty())
                updateTotalAmount(binding.edtClaimedMiles2.text.toString().toDouble(),binding.edtMileageRate.text.toString().toDouble())
            }
        })

        binding.edtTax.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.edtTax.text.isNotEmpty()&&binding.edtTotalAmount.text.isNotEmpty())
                updateNetAmount(binding.edtTotalAmount.text.toString().toDouble(), binding.edtTax.text.toString().toDouble())
            }
        })
        binding.edtPetrolAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               // updateNetAmount(binding.edtTotalAmount.getNumericValue(), binding.edtTax.getNumericValue(),binding.edtParkAmount.getNumericValue())
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
                       countryName= place.addressComponents?.asList()?.find { it.types.contains("country") }?.name.toString()

                        if(countryName=="United States"){
                           binding.spnMileageType.setSelection(0)

                        }else{
                            binding.spnMileageType.setSelection(1)
                        }


                        binding.tvTripFrom.text = place.address
                        fromadd= place.address.toString()

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
                        Log.d(TAG, "setupAutoCompletePlaces: Place: ${place.address}, ${place.id}")
                        Log.d(TAG, "setupAutoCompletePlaces: Place add: ${place}")
                        binding.tvTripTo.text = place.address
                        toadd= place.address.toString()
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
                if (element.tax_code == mileageDetail.tax_code) {
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
    private fun setupExpenceGroup(){
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
                    if(it.expenseGroupID == groupid&&(it.companyID==compnyId.toString())){
                        viewModel.expenseTypeList.add(it)
                        // println("selected expenseTypeList Added :" )

                    }else if(it.companyID.isNullOrEmpty()){
                        //viewModel.expenseTypeList.add(it)

                    }
                }
                setupExpenceType()


            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

    }
    private fun setupExpenceType(){
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
                //expenseCode=viewModel.expenseTypeList.get(position).activityCode
                mtaxcodeId= viewModel.expenseTypeList[position].taxCodeID
                expenseCodeId= viewModel.expenseTypeList[position].expenseCodeID
                setupTax()
                viewModel.expenseCode.forEach {
                    if(it.id.toString() == expenseCodeId) {
                        expenseCode=it.expenseCode

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
    private fun openAutoCompletePlaces(isFromLocation: Boolean){
        contextActivity?.let {
            val fields = listOf(Place.Field.ID, Place.Field.ADDRESS,Place.Field.ADDRESS_COMPONENTS)
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
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"milage",this)
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun createNewClaim() {
        try {
            var dateFormate = if(companyDateFormate=="USA") {
                Constants.MMM_DD_YYYY_FORMAT
            }else{
                Constants.DD_MM_YYYY_FORMAT

            }

            if (binding.edtMileageRate.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please Enter Mileage Rate", Toast.LENGTH_LONG).show()
                onCreateClaimFailed()
                return
            }
            val carType: CarType? =
                viewModel.carTypeList.find { it.type == binding.edtCarType.text.toString() }
            val carTypePos = viewModel.carTypeList.indexOf(carType)
            val claimRequest = viewModel.getMileageEditRequest(
                binding.edtTitle.text.toString().trim(),
                if (!viewModel.companyList.isNullOrEmpty()) viewModel.companyList[binding.spnCompanyName.selectedItemPosition].id else "",
                if (!viewModel.mileageTypeList.isNullOrEmpty()) viewModel.mileageTypeList[binding.spnMileageType.selectedItemPosition].id else "",
                if (!viewModel.departmentList.isNullOrEmpty()) viewModel.departmentList[binding.spnDepartment.selectedItemPosition].id else "",
                submitDate,
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].id else "",
                binding.edtMerchantName.text.toString().trim(),
                tripDate,
                binding.tvTripFrom.text.toString().trim(),
                binding.tvTripTo.text.toString().trim(),
               // "1",/*binding.spnDistance*/
                binding.edtClaimedMiles.text.toString(),
                if (!viewModel.carTypeList.isNullOrEmpty()) viewModel.carTypeList[carTypePos].id else "", //spnCurrency.selectedItemPosition,
                binding.edtClaimedMiles2.text.toString().trim(),
                binding.switchRoundTrip.isChecked,
                if (!viewModel.currencyList.isNullOrEmpty()) viewModel.currencyList[binding.spnCurrency.selectedItemPosition].id else "",
                binding.edtPetrolAmount.text.toString().trim(),
                binding.edtParkAmount.text.toString().trim(),
                binding.edtTotalAmount.text.toString().trim(),
                binding.edtTax.text.toString().trim(),
                binding.tvNetAmount.text.toString().trim(),
                binding.edtDescription.text.toString().trim(),
                if (!taxcodeId.isEmpty()) taxcodeId else "",
                binding.edtAutionValue.text.toString().trim(),
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseCodeID else "",
                binding.edtMileageRate.text.toString().trim(),
                viewModel.attachmentsList as List<String>,
                if (!viewModel.expenseTypeList.isNullOrEmpty()) viewModel.expenseTypeList[binding.spnExpenseType.selectedItemPosition].expenseGroupID else "",
                mileageDetail.id,
                mileageDetail.overall_status_id,
                mileageDetail.rm_status_id,
                mileageDetail.fm_status_id
                )

            if (!validateEditClaim(claimRequest)) {
                onCreateClaimFailed()
                return
            }
            if (binding.tvDateOfSubmission.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please select Date", Toast.LENGTH_LONG).show()
                onCreateClaimFailed()
                return
            }
            if (binding.tvDateOfTrip.text.isNullOrEmpty()) {
                Toast.makeText(contextActivity, "Please select Date Of Trip", Toast.LENGTH_LONG).show()
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
            editMileageObserver(claimRequest)
           /* if(viewModel.attachmentsList.size > 0){
                binding.btnSubmit.visibility = View.GONE
                uploadAttachement(claimRequest)
            } else{
                Toast.makeText(contextActivity, "Please select receipt image to upload", Toast.LENGTH_LONG).show()
                return
            }*/

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
                        binding.mProgressBars.visibility = View.GONE
                        var distanceKmMiles=0.0

                        resource.data?.let { response ->
                            val distance=
                                response.rows[0].elements.get(0).distance?.value?.let { it1 ->
                                    meterToKiloMeter(
                                        it1
                                    )
                                }
                            if(countryName=="United States"){
                                binding.spnMileageType.setSelection(0)
                                if (distance != null) {
                                    distanceKmMiles = distance
                                }
                            }else{
                                binding.spnMileageType.setSelection(1)
                                if (distance != null) {
                                    distanceKmMiles = distance/1.609
                                }
                            }
                            val decimalmiles = distanceKmMiles.let { it1 -> BigDecimal(it1).setScale(1, RoundingMode.HALF_EVEN) }
                            if(!isennitial){
                                isennitial=false
                                binding.edtClaimedMiles.setText(decimalmiles.toString())
                                binding.edtClaimedMiles2.setText(decimalmiles.toString())
                            }

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
            viewModel.departmentListCompany = dropdownResponse.departmentList
            ( dropdownResponse.expenseGroup as MutableList<ExpenseGroup>).forEachIndexed { index, expenseGroup ->

                if(expenseGroup.name.contains("Mileage")){
                    viewModel.expenseGroupList.add(expenseGroup)
                }
            }
            viewModel.expenseCode = dropdownResponse.expenseCode as MutableList<ExpenseCode>
            viewModel.expenseTypeListExpenseGroup = dropdownResponse.expenseType as MutableList<ExpenseType>
            viewModel.distanceList = dropdownResponse.expenseGroup
            viewModel.carTypeList = dropdownResponse.carType
            viewModel.currencyList = dropdownResponse.currencyType as MutableList<Currency>
            viewModel.mileageTypeList = dropdownResponse.mileageType
            viewModel.taxList = dropdownResponse.tax
            viewModel.MileageRateList = dropdownResponse.milageRate
            println("MileageRateList :"+viewModel.MileageRateList)

            setupSpinners()
        }
    }

    private fun updateNetAmount(total: Double, tax: Double){
        try {
            val totalAmount = total
            val taxAmount =tax



            val netAmount = totalAmount - taxAmount
            if(netAmount<0){
                Toast.makeText(contextActivity, "Net amount should be greater than 0", Toast.LENGTH_SHORT).show()
                binding.tvNetAmount.setText("")

            }else{
                binding.tvNetAmount.setText(String.format("%.2f", netAmount))

               // binding.tvNetAmount.setText(netAmount.toString())

            }
            //binding.tvNetAmount.text = "$netAmount"
        }
        catch (error: Exception){
            Log.e(TAG, "updateNetAmount: ${error.message}")
        }
    }
    private fun updateTotalAmount(Distance: Double, mileageRate: Double){
        try {
            val fare= Distance.times(mileageRate)
            val decimal = fare.let { it1 -> BigDecimal(it1).setScale(2, RoundingMode.HALF_EVEN) }
            binding.edtTotalAmount.setText(String.format("%.2f",decimal.toString().toDouble()))

        }
        catch (error: Exception){
            Log.e(TAG, "totalAmount: ${error.message}")
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
    private fun showAleartDialog(message:String){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, id -> dialog.dismiss()
            })
            // negative button text and action
            /*.setNegativeButton("Cancel", DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
            })*/
        val alert = dialogBuilder.create()
        alert.setTitle("Mileage Rate")
        alert.show()
    }

    private fun uploadAttachement(mileageClaimRequest: NewMileageClaimRequest){
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("claim_type", "M")

        for (photoPath in viewModel.attachmentsList) {
            if (photoPath != null) {
                val images = File(photoPath)
                if (images.exists()) {
                   // val bitmap = BitmapFactory.decodeFile(photoPath)
                   // val imgFile= bitmapToFile(bitmap,images.name)
                   // builder.addFormDataPart("claimImage", images.name, RequestBody.create(MultipartBody.FORM,imgFile))
                    builder.addFormDataPart("claimImage", images.name, RequestBody.create(
                        MultipartBody.FORM, images))
                }
            }
        }

        val mrequestBody: RequestBody = builder.build()

        println("request dat:$mrequestBody")
        viewModel.uploadClaimAttachement(mrequestBody).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                mileageClaimRequest.attachments=response.images
                               // setCreateClaimObserver(mileageClaimRequest)


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
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileNameToSave)
            file.createNewFile()

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

    private fun editMileageObserver(mileageClaimRequest: EditMileageClaimRequest) {
        viewModel.editNewMileageClaim(mileageClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "editMileageObserver: ${resource.status}")

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
    private fun validateEditClaim(newClaimRequest: EditMileageClaimRequest): Boolean {
        val isValid = viewModel.validateEditClaimRequest(newClaimRequest)
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
           // (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }

    private fun onCreateClaimFailed() {
        binding.btnSubmit.isEnabled = true
    }

    private fun showCalenderDialog(isDateOfSubmission: Boolean){
        val calendar = Calendar.getInstance()
        val calendarStart: Calendar = Calendar.getInstance()
//        val calendarEnd: Calendar = Calendar.getInstance()

        //calendarStart.set(calendar[Calendar.YEAR], calendar[Calendar.MONTH] - 1, calendar[Calendar.DAY_OF_MONTH])
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
            if(isDateOfSubmission) {
                binding.tvDateOfSubmission.text = date
                binding.tvDateOfTrip.text = date

                submitDate =Utils.getDateInDisplayFormatWithCountry2(it)
                tripDate =Utils.getDateInDisplayFormatWithCountry2(it)


            } else {
                binding.tvDateOfSubmission.text = date
                binding.tvDateOfTrip.text = date
            }

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
                //.setMinFileSize(50) // Restrict by minimum file size
                //.setMaxFileSize(100) //  Restrict by maximum file size
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
    private fun meterToMiles(meter:Int):Double{
       val CONVERSION_UNIT = 0.00062137119
        return meter * CONVERSION_UNIT
    }
    private fun meterToKiloMeter(meter:Int):Double{
        return meter * 0.001
    }
    private fun getMileageRate(compnyid:Int):Double{
        var uniteprice="0"
        viewModel.MileageRateList.forEach {
            println("it.company_id :"+it.company_id +" And compnyid "+compnyid)
            if(it.company_id==compnyid){
                uniteprice=it.unit_price
                return@forEach
            }
        }
        return uniteprice.toDouble()
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
    override fun callback(action: String, data: Any, postion: Int) {
        if (action == "show") {
            showImagePopup(data as String)
        }
    }


}