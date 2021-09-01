package com.bonhams.expensemanagement.ui.claims.splitClaim

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils


class SplitClaimFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null

    private lateinit var binding: FragmentSplitClaimBinding
    private lateinit var viewModel: SplitClaimViewModel
    private lateinit var newClaimViewModel: NewClaimViewModel
    private lateinit var claimRequest: NewClaimRequest

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_split_claim, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setClickListeners()
        setupSpinners()
        setupView()

        return view
    }

    fun setClaimRequestDetail(request: NewClaimRequest){
        claimRequest = request
    }

    private fun setClickListeners(){
        binding.layoutAddSplit.setOnClickListener(View.OnClickListener {
            createNewSplitLayout()
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

    private fun setupView(){
        binding.tvMerchantName.text = claimRequest.merchantName
        binding.tvDate.text = claimRequest.dateSubmitted
        binding.tvUserName.text = AppPreferences.fullName
        binding.tvTotalAmount.text = claimRequest.totalAmount
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            SplitClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(SplitClaimViewModel::class.java)

        newClaimViewModel = ViewModelProvider(requireActivity(),
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }

    private fun setupSpinners(){
        // Company Adapter
        val companyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.companyList
        )
        binding.spnCompany.adapter = companyAdapter
        binding.spnCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.companyList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        val departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.departmentList
        )
        binding.spnDepartment.adapter = departmentAdapter
        binding.spnDepartment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.departmentList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Adapter
        val expenseAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.expenseTypeList
        )
        binding.spnExpense.adapter = expenseAdapter
        binding.spnExpense.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.expenseTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }
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

    private fun createNewSplitLayout(){
        val view = View.inflate(contextActivity, R.layout.item_claims_split, null)
        val spnCompany = view.findViewById<Spinner>(R.id.spnCompany)
        val spnDepartment = view.findViewById<Spinner>(R.id.spnDepartment)
        val spnExpense = view.findViewById<Spinner>(R.id.spnExpense)
        val edtTotalAmount = view.findViewById<EditText>(R.id.edtTotalAmount)
        val edtTaxCode = view.findViewById<EditText>(R.id.edtTaxCode)

        // Company Adapter
        val companyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.expenseGroupList
        )
        spnCompany?.adapter = companyAdapter
        spnCompany?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        var departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.expenseGroupList
        )
        spnDepartment?.adapter = departmentAdapter
        spnDepartment?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Adapter
        var expenseAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.expenseGroupList
        )
        spnExpense?.adapter = expenseAdapter
        spnExpense?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.expenseGroupList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        binding.layoutSplitDetail.addView(view)
    }

    private fun createNewClaim() {
        if (validateCreateClaim()) {
            onCreateClaimFailed()
            return
        }

        binding.btnSubmit.visibility = View.GONE
        setCreateClaimObserver(claimRequest)
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

}