package com.bonhams.expensemanagement.ui.claims.newClaim

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.SpinnerItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.utils.Constants
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


class NewClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var edtMerchantName: EditText? = null
    private var spnExpenseGroup: Spinner? = null
    private var spnExpenseType: Spinner? = null
    private var edtCompanyNumber: EditText? = null
    private var spnDepartment: Spinner? = null
    private var tvDateOfSubmission: TextView? = null
    private var spnCurrency: Spinner? = null
    private var edtTotalAmount: EditText? = null
    private var edtTax: EditText? = null
    private var edtNetAmount: EditText? = null
    private var edtDescription: EditText? = null
    private var tvUploadPic: TextView? = null
    private var tvNoFileSelected: TextView? = null
    private var ivPicUpload: ImageView? = null
    private var rvAttachments: RecyclerView? = null
    private var mProgressBar: ProgressBar? = null
    private var btnSplit: AppCompatButton? = null
    private var btnSubmit: AppCompatButton? = null
    private var attachments = "1, 2, 3"

    private lateinit var viewModel: NewClaimViewModel
    private lateinit var expenseGroupAdapter: ArrayAdapter<SpinnerItem>
    private lateinit var expenseTypeAdapter: ArrayAdapter<SpinnerItem>
    private lateinit var departmentAdapter: ArrayAdapter<SpinnerItem>
    private lateinit var currencyAdapter: ArrayAdapter<SpinnerItem>
    private lateinit var attachmentsAdapter: AttachmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_claim, container, false)
        contextActivity = activity as? BaseActivity

        edtMerchantName = view.findViewById(R.id.edtMerchantName)
        spnExpenseGroup = view.findViewById(R.id.spnExpenseGroup)
        spnExpenseType = view.findViewById(R.id.spnExpenseType)
        edtCompanyNumber = view.findViewById(R.id.edtCompanyNumber)
        spnDepartment = view.findViewById(R.id.spnDepartment)
        tvDateOfSubmission = view.findViewById(R.id.tvDateOfSubmission)
        spnCurrency = view.findViewById(R.id.spnCurrency)
        edtTotalAmount = view.findViewById(R.id.edtTotalAmount)
        edtTax = view.findViewById(R.id.edtTax)
        edtNetAmount = view.findViewById(R.id.edtNetAmount)
        edtDescription = view.findViewById(R.id.edtDescription)
        tvUploadPic = view.findViewById(R.id.tvUploadPic)
        tvNoFileSelected = view.findViewById(R.id.tvNoFileSelected)
        ivPicUpload = view.findViewById(R.id.ivPicUpload)
        rvAttachments = view.findViewById(R.id.rvAttachments)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        btnSplit = view.findViewById(R.id.btnSplit)
        btnSubmit = view.findViewById(R.id.btnSubmit)


        setupViewModel()
        setClickListeners()
        setupSpinners()
        setupAttachmentRecyclerView();

        return view
    }

    private fun setClickListeners(){
        tvUploadPic?.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })
        ivPicUpload?.setOnClickListener(View.OnClickListener {
            showBottomSheet()
        })

        tvDateOfSubmission?.setOnClickListener(View.OnClickListener {
            showCalenderDialog()
        })

        btnSplit?.setOnClickListener(View.OnClickListener {
             val fragment = SplitClaimFragment()
            contextActivity?.supportFragmentManager?.beginTransaction()?.add(
                R.id.container,
                fragment,
                fragment.javaClass.simpleName
            )?.addToBackStack(fragment.javaClass.simpleName)?.commit()
        })

        btnSubmit?.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                if(NoInternetUtils.isConnectedToInternet(it))
                    createNewClaim()
                else
                    Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinners(){
        initializeSpinnerItems();

        // Expense Group Adapter
        expenseGroupAdapter = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.expenseGroupList
        )
        spnExpenseGroup?.adapter = expenseGroupAdapter
        spnExpenseGroup?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Type Adapter
        expenseTypeAdapter = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.expenseTypeList
        )
        spnExpenseType?.adapter = expenseTypeAdapter
        spnExpenseType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        departmentAdapter = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.departmentList
        )
        spnDepartment?.adapter = departmentAdapter
        spnDepartment?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.departmentList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Currency Adapter
        currencyAdapter = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.currencyList
        )
        spnCurrency?.adapter = currencyAdapter
        spnCurrency?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.currencyList[position].title
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
        rvAttachments?.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList)
        rvAttachments?.adapter = attachmentsAdapter
    }

    private fun refreshAttachments(){
        if(viewModel.attachmentsList.size > 0){
            tvNoFileSelected?.visibility = View.GONE
            rvAttachments?.visibility = View.VISIBLE
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            rvAttachments?.visibility = View.GONE
            tvNoFileSelected?.visibility = View.VISIBLE
        }
    }

    private fun initializeSpinnerItems(){
        viewModel.expenseGroupList =
            listOf(SpinnerItem("1", "Group 1"), SpinnerItem("2", "Group 2"),
                SpinnerItem("3", "Group 3"), SpinnerItem("4", "Group 4"))

        viewModel.expenseTypeList =
            listOf(SpinnerItem("1", "Group Type 1"), SpinnerItem("2", "Group Type 2"),
                SpinnerItem("3", "Group Type 3"), SpinnerItem("4", "Group Type 4"))

        viewModel.departmentList =
            listOf(SpinnerItem("1", "Department 1"), SpinnerItem("2", "Department 2"),
                SpinnerItem("3", "Department 3"), SpinnerItem("4", "Department 4"))

        viewModel.currencyList =
            listOf(SpinnerItem("1", "Currency 1"), SpinnerItem("2", "Currency 2"),
                SpinnerItem("3", "Currency 3"), SpinnerItem("4", "Currency 4"))
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }

    private fun setCreateClaimObserver(newClaimRequest: NewClaimRequest) {
        viewModel.createNewClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
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
                        mProgressBar?.visibility = View.GONE
                        btnSubmit?.visibility = View.VISIBLE
                        Log.e(TAG, "setChangePasswordObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        mProgressBar?.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

//        attachments = viewModel.attachmentsList.joinToString { it }

        btnSubmit?.visibility = View.GONE
        val changePasswordRequest = viewModel.getNewClaimRequest(
            edtMerchantName?.text.toString().trim(),
            viewModel.expenseGroupList[spnExpenseGroup?.selectedItemPosition!!].id,
            viewModel.expenseTypeList[spnExpenseType?.selectedItemPosition!!].id,
            edtCompanyNumber?.text.toString().trim(),
            viewModel.departmentList[spnDepartment?.selectedItemPosition!!].id,
            Utils.getFormattedDate(tvDateOfSubmission?.text.toString().trim(), Constants.DD_MMM_YYYY_FORMAT),
            viewModel.currencyList[spnCurrency?.selectedItemPosition!!].id,
            edtTotalAmount?.text.toString().trim(),
            edtTax?.text.toString().trim(),
            edtNetAmount?.text.toString().trim(),
            edtDescription?.text.toString().trim(),
            attachments,
            attachments
        )
        setCreateClaimObserver(changePasswordRequest)
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
        mProgressBar?.visibility = View.GONE
        btnSubmit?.visibility = View.VISIBLE
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
    }

    private fun onCreateClaimFailed() {
        btnSubmit?.isEnabled = true
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
            val date = Utils.getDateInDisplayFormat(it)
            Log.d("DatePicker Activity", "Date String = ${date}:: Date epoch value = ${it}")
            tvDateOfSubmission?.text = date
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