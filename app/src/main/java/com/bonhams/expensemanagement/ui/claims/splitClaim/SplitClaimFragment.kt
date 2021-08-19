package com.bonhams.expensemanagement.ui.claims.splitClaim

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
import com.bonhams.expensemanagement.data.model.SpinnerItem
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Status
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils


class SplitClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private var tvMerchantName: TextView? = null
    private var tvDate: TextView? = null
    private var tvUserName: TextView? = null
    private var tvTotalAmount: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var btnSubmit: AppCompatButton? = null
    private var layoutSplitDetail: LinearLayout? = null
    private var layoutAddSplit: RelativeLayout? = null

    private lateinit var viewModel: SplitClaimViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_split_claim, container, false)
        contextActivity = activity as? BaseActivity

        tvMerchantName = view.findViewById(R.id.tvMerchantName)
        tvDate = view.findViewById(R.id.tvDate)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        mProgressBar = view.findViewById(R.id.mProgressBars)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        layoutSplitDetail = view.findViewById(R.id.layoutSplitDetail)
        layoutAddSplit = view.findViewById(R.id.layoutAddSplit)


        setupViewModel()
        setClickListeners()
        initializeSpinnerItems()

        return view
    }

    private fun setClickListeners(){
        layoutAddSplit?.setOnClickListener(View.OnClickListener {
            createNewSplitLayout()
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
            SplitClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SplitClaimViewModel::class.java)
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

    private fun createNewSplitLayout(){
        val view = View.inflate(contextActivity, R.layout.item_claims_split, null)
        val spnCompany = view.findViewById<Spinner>(R.id.spnCompany)
        val spnDepartment = view.findViewById<Spinner>(R.id.spnDepartment)
        val spnExpense = view.findViewById<Spinner>(R.id.spnExpense)
        val edtTotalAmount = view.findViewById<EditText>(R.id.edtTotalAmount)
        val edtTaxCode = view.findViewById<EditText>(R.id.edtTaxCode)

        // Company Adapter
        var companyAdapter: ArrayAdapter<SpinnerItem> = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.expenseGroupList
        )
        spnCompany?.adapter = companyAdapter
        spnCompany?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        var departmentAdapter: ArrayAdapter<SpinnerItem> = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.expenseGroupList
        )
        spnDepartment?.adapter = departmentAdapter
        spnDepartment?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Adapter
        var expenseAdapter: ArrayAdapter<SpinnerItem> = ArrayAdapter<SpinnerItem>(
            requireContext(),
            R.layout.item_spinner, R.id.title,
            viewModel.expenseGroupList
        )
        spnExpense?.adapter = expenseAdapter
        spnExpense?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = viewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        layoutSplitDetail?.addView(view)
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

//        attachments = viewModel.attachmentsList.joinToString { it }

//        btnSubmit?.visibility = View.GONE
        /*val changePasswordRequest = viewModel.getNewClaimRequest(
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
        setCreateClaimObserver(changePasswordRequest)*/
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