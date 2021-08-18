package com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim

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
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.switchmaterial.SwitchMaterial
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class NewMileageClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var edtCompanyName: EditText? = null
    private var edtMileageType: EditText? = null
    private var spnDepartment: Spinner? = null
    private var tvDateOfSubmission: TextView? = null
    private var spnExpenseType: Spinner? = null
    private var edtMerchantName: EditText? = null
    private var tvDateOfTrip: TextView? = null
    private var edtTripFrom: TextView? = null
    private var edtTripTo: TextView? = null

    private var spnDistance: Spinner? = null
    private var spnCarType: Spinner? = null
    private var edtClaimedMiles: EditText? = null
    private var switchRoundTrip: SwitchMaterial? = null

    private var spnCurrency: Spinner? = null
    private var edtPetrolAmount: EditText? = null
    private var edtParkAmount: EditText? = null
    private var edtTotalAmount: EditText? = null
    private var edtTax: EditText? = null
    private var edtNetAmount: EditText? = null
    private var edtDescription: EditText? = null
    private var tvNoFileSelected: TextView? = null
    private var ivPicUpload: ImageView? = null
    private var mProgressBar: ProgressBar? = null
    private var btnSubmit: AppCompatButton? = null
    private lateinit var viewModel: NewMileageClaimViewModel
    private var attachments = "1, 2, 3"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_mileage_claim, container, false)
        contextActivity = activity as? BaseActivity

        edtCompanyName = view.findViewById(R.id.edtCompanyName)
        edtMileageType = view.findViewById(R.id.edtMileageType)
        spnDepartment = view.findViewById(R.id.spnDepartment)
        tvDateOfSubmission = view.findViewById(R.id.tvDateOfSubmission)
        spnExpenseType = view.findViewById(R.id.spnExpenseType)
        edtMerchantName = view.findViewById(R.id.edtMerchantName)
        tvDateOfTrip = view.findViewById(R.id.tvDateOfTrip)
        edtTripFrom = view.findViewById(R.id.edtTripFrom)
        edtTripTo = view.findViewById(R.id.edtTripTo)
        spnDistance = view.findViewById(R.id.spnDistance)
        spnCarType = view.findViewById(R.id.spnCarType)
        edtClaimedMiles = view.findViewById(R.id.edtClaimedMiles)
        switchRoundTrip = view.findViewById(R.id.switchRoundTrip)
        spnCurrency = view.findViewById(R.id.spnCurrency)
        edtPetrolAmount = view.findViewById(R.id.edtPetrolAmount)
        edtParkAmount = view.findViewById(R.id.edtParkAmount)
        edtTotalAmount = view.findViewById(R.id.edtTotalAmount)
        edtTax = view.findViewById(R.id.edtTax)
        edtNetAmount = view.findViewById(R.id.edtNetAmount)
        edtDescription = view.findViewById(R.id.edtDescription)
        tvNoFileSelected = view.findViewById(R.id.tvNoFileSelected)
        ivPicUpload = view.findViewById(R.id.ivPicUpload)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        setupViewModel()
        setClickListeners()

        return view
    }

    private fun setClickListeners(){
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
            NewMileageClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewMileageClaimViewModel::class.java)
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

        btnSubmit?.visibility = View.GONE
        val changePasswordRequest = viewModel.getNewMileageClaimRequest(
            edtCompanyName?.text.toString().trim(),
            edtMileageType?.text.toString().trim(),
            "1",
            "12/08/2021",//tvDateOfSubmission?.text.toString().trim(),
            "1",
            edtMerchantName?.text.toString().trim(),
            "12/08/2021",//tvDateOfTrip?.text.toString().trim(),
            "test",//edtTripFrom?.text.toString().trim(),
            "test", //edtTripTo?.text.toString().trim(),
            "1",//tvDateOfSubmission?.text.toString().trim(),
            "1", //spnCurrency.selectedItemPosition,
            edtClaimedMiles?.text.toString().trim(),
            switchRoundTrip?.isChecked.toString(),
            "1",
            edtPetrolAmount?.text.toString().trim(),
            edtParkAmount?.text.toString().trim(),
            edtTotalAmount?.text.toString().trim(),
            edtTax?.text.toString().trim(),
            edtNetAmount?.text.toString().trim(),
            edtDescription?.text.toString().trim(),
            attachments
        )
        setCreateClaimObserver(changePasswordRequest)
    }

    private fun setCreateClaimObserver(mileageClaimRequest: NewMileageClaimRequest) {
        viewModel.createNewMileageClaim(mileageClaimRequest).observe(this, Observer {
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
}