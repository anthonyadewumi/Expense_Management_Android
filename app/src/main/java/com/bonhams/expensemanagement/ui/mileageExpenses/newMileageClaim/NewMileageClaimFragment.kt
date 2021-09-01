package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.FragmentNewMileageClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import com.bonhams.expensemanagement.utils.Utils
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
import java.util.*

class NewMileageClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var binding: FragmentNewMileageClaimBinding
    private lateinit var viewModel: NewMileageClaimViewModel

    private lateinit var departmentAdapter: CustomSpinnerAdapter
    private lateinit var expenseTypeAdapter: CustomSpinnerAdapter
    private lateinit var distanceAdapter: CustomSpinnerAdapter
    private lateinit var carTypeAdapter: CustomSpinnerAdapter
    private lateinit var currencyAdapter: CustomSpinnerAdapter
    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private var attachments = "1, 2, 3"

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

        return view
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
            NewMileageClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewMileageClaimViewModel::class.java)
    }

    private fun setupSpinners(){
        // Department Adapter
        departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
        binding.spnDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.departmentList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Type Adapter
        expenseTypeAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.expenseTypeList
        )
        binding.spnExpenseType.adapter = expenseTypeAdapter
        binding.spnExpenseType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


        // Distance Adapter
        distanceAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.distanceList
        )
        binding.spnDistance.adapter = distanceAdapter
        binding.spnDistance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.distanceList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }


        // Car Type Adapter
        carTypeAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.carTypeList
        )
        binding.spnCarType.adapter = carTypeAdapter
        binding.spnCarType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.carTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Currency Adapter
        currencyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            viewModel.currencyList
        )
        binding.spnCurrency.adapter = currencyAdapter
        binding.spnCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.currencyList[position].name
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
    }

    private fun setupAttachmentRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttachments.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList)
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

        binding.btnSubmit.visibility = View.GONE
        val changePasswordRequest = viewModel.getNewMileageClaimRequest(
            binding.edtCompanyName.text.toString().trim(),
            binding.edtMileageType.text.toString().trim(),
            "1",
            "12/08/2021",//tvDateOfSubmission?.text.toString().trim(),
            "1",
            binding.edtMerchantName.text.toString().trim(),
            "12/08/2021",//tvDateOfTrip?.text.toString().trim(),
            "test",//edtTripFrom?.text.toString().trim(),
            "test", //edtTripTo?.text.toString().trim(),
            "1",//tvDateOfSubmission?.text.toString().trim(),
            "1", //spnCurrency.selectedItemPosition,
            binding.edtClaimedMiles.text.toString().trim(),
            binding.switchRoundTrip.isChecked.toString(),
            "1",
            binding.edtPetrolAmount.text.toString().trim(),
            binding.edtParkAmount.text.toString().trim(),
            binding.edtTotalAmount.text.toString().trim(),
            binding.edtTax.text.toString().trim(),
            binding.edtNetAmount.text.toString().trim(),
            binding.edtDescription.text.toString().trim(),
            attachments
        )
        setCreateClaimObserver(changePasswordRequest)
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
        viewModel.departmentList = dropdownResponse.departmentList
        viewModel.expenseTypeList = dropdownResponse.expenseType
        viewModel.distanceList = dropdownResponse.expenseGroup
        viewModel.carTypeList  = dropdownResponse.carType
        viewModel.currencyList  = dropdownResponse.currencyType

        setupSpinners()
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

    private fun validateCreateClaim(): Boolean {
        /*edtOldPassword?.error = viewModel.validatePassword(
            edtOldPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password),
            true
        )

        edtNewPassword?.error = viewModel.validatePassword(
            edtNewPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password),
            false
        )

        edtConfirmPassword?.error = viewModel.validateConfirmPassword(
            edtNewPassword?.text.toString().trim(),
            edtConfirmPassword?.text.toString().trim(),
            resources.getString(R.string.validate_password_not_match)
        )

        return (viewModel.validOldPassword || viewModel.validPassword || viewModel.validConfirmPassword)*/
        return false
    }

    private fun setResponse(commonResponse: CommonResponse) {
        binding.mProgressBars.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
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
            val view = layoutInflater.inflate(R.layout.item_bottom_sheet_picture, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            val bottomOptionOne = view.findViewById<TextView>(R.id.bottomOptionOne)
            val bottomOptionTwo = view.findViewById<TextView>(R.id.bottomOptionTwo)
            val bottomOptionCancel = view.findViewById<TextView>(R.id.bottomOptionCancel)

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
}