package com.bonhams.expensemanagement.ui.claims.newClaim

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.Status
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils


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
    private var tvNoFileSelected: TextView? = null
    private var ivPicUpload: ImageView? = null
    private var mProgressBar: ProgressBar? = null
    private var btnSplit: AppCompatButton? = null
    private var btnSubmit: AppCompatButton? = null
    private lateinit var viewModel: NewClaimViewModel
    private var attachments = "1, 2, 3"

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
        tvNoFileSelected = view.findViewById(R.id.tvNoFileSelected)
        ivPicUpload = view.findViewById(R.id.ivPicUpload)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        btnSplit = view.findViewById(R.id.btnSplit)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        (contextActivity as MainActivity).setAppbarTitle(getString(R.string.create_new_claim))
        (contextActivity as MainActivity).showAppbarBackButton(true)
        (contextActivity as MainActivity).showAppbarSearch(false)

        setupViewModel()
        setClickListeners()

        return view
    }

    private fun setClickListeners(){
        btnSplit?.setOnClickListener(View.OnClickListener {

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

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

        btnSubmit?.visibility = View.GONE
        val changePasswordRequest = viewModel.getNewClaimRequest(
            edtMerchantName?.text.toString().trim(),
            "1", //spnExpenseGroup.selectedItemPosition,
            "1", //spnExpenseType.selectedItemPosition,
            edtCompanyNumber?.text.toString().trim(),
            "1", //spnDepartment.selectedItemPosition,
            "12-08-2021",//tvDateOfSubmission?.text.toString().trim(),
            "1", //spnCurrency.selectedItemPosition,
            edtTotalAmount?.text.toString().trim(),
            edtTax?.text.toString().trim(),
            edtNetAmount?.text.toString().trim(),
            edtDescription?.text.toString().trim(),
            attachments,
            attachments
        )
        setCreateClaimObserver(changePasswordRequest)
    }

    private fun setCreateClaimObserver(newClaimRequest: NewClaimRequest) {
        viewModel.createNewClaim(newClaimRequest).observe(this, Observer {
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

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(galleryIntent, 100)
    }

    private fun takePhotoFromCamera(){

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "onRequestPermissionsResult: ${permissions[i]}")
                    //granted
                    choosePhotoFromGallery()
                }
            }
        }
        else if (requestCode == 101 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "onRequestPermissionsResult: ${permissions[i]}")
                    //granted
                    takePhotoFromCamera()
                }
            }
        }
    }
}