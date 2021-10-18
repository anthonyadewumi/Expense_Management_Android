package com.bonhams.expensemanagement.ui.claims.splitClaim

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.adapters.SplitAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
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
    private lateinit var splitAdapter: SplitAdapter
    companion object {
       public var splitItmlist: MutableList<SplitClaimItem> = mutableListOf()

    }
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
        setupSplitRecyclerView()
        setupSpinners()
        setupView()

        return view
    }

    override fun onResume() {
        super.onResume()
        System.out.println("call fregment")
            viewModel.splitList.add("add more ")
            splitAdapter.notifyDataSetChanged()

    }
    fun setClaimRequestDetail(request: NewClaimRequest){
        claimRequest = request
    }

    private fun setClickListeners(){
        binding.layoutAddSplit.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), SplitClaimActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

           // createNewSplitLayout()
        })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                if (NoInternetUtils.isConnectedToInternet(it))
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
    private fun setupSplitRecyclerView(){
        repeat(3) { index ->
            viewModel.splitList.add("Split $index")
        }
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvsplit.layoutManager = linearLayoutManager
        splitAdapter = SplitAdapter(splitItmlist as MutableList<SplitClaimItem?>)
        binding.rvsplit.adapter = splitAdapter
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
                                Log.d(TAG, "setCreateClaimObserver: ${resource.status}")
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "setCreateClaimObserver: ${it.message}")
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
        var expenseCode: String = ""

        val view = View.inflate(contextActivity, R.layout.item_claims_split, null)
        val linearLayout = view.findViewById<LinearLayout>(R.id.layoutSplit)
        val spnCompany = view.findViewById<Spinner>(R.id.spnCompany)
        val spnDepartment = view.findViewById<Spinner>(R.id.spnDepartment)
        val spnExpense = view.findViewById<Spinner>(R.id.spnExpense)
        val edtAutionValue = view.findViewById<EditText>(R.id.edtAutionValue)
        val tvAuctionExpCode = view.findViewById<TextView>(R.id.tvAuctionExpCode)
        val edtTotalAmount = view.findViewById<EditText>(R.id.edtTotalAmount)
        val edtTaxCode = view.findViewById<EditText>(R.id.edtTaxCode)

        // Company Adapter
        val companyAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.companyList
        )
        spnCompany?.adapter = companyAdapter
        spnCompany?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.companyList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Department Adapter
        var departmentAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.departmentList
        )
        spnDepartment?.adapter = departmentAdapter
        spnDepartment?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val item = newClaimViewModel.departmentList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        // Expense Adapter
        var expenseAdapter = CustomSpinnerAdapter(
            requireContext(),
            R.layout.item_spinner,
            newClaimViewModel.expenseTypeList
        )
        spnExpense?.adapter = expenseAdapter
        spnExpense?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            View.OnFocusChangeListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                expenseCode=newClaimViewModel.expenseTypeList.get(position).activityCode
                if(!binding.edtAutionValue.text.toString().isEmpty()){
                    tvAuctionExpCode.text = expenseCode

                }else{
                    tvAuctionExpCode.text = ""
                }
               // val item = newClaimViewModel.expenseTypeList[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
            override fun onFocusChange(v: View?, hasFocus: Boolean) {}
        }

        edtAutionValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!binding.edtAutionValue.text.toString().isEmpty()){
                    tvAuctionExpCode.text = expenseCode

                }else{
                    tvAuctionExpCode.text = ""
                }
            }
        })
        viewModel.splitCount = viewModel.splitCount + 1
        linearLayout.tag = viewModel.splitCount

        binding.layoutSplitDetail.addView(view)
    }

    private fun createNewClaim() {
        if(!validateAllSplitDetails()){
            onCreateClaimFailed()
            return
        }

        binding.btnSubmit.visibility = View.GONE
        setCreateClaimObserver(claimRequest)
    }

    private fun validateAllSplitDetails(): Boolean{
        var isValid = true
        Log.d(TAG, "validateAllSplitDetails viewModel.splitCount : ${viewModel.splitCount}")
        claimRequest.split.clear()

        if(viewModel.splitCount <= 0){
            val companyOne = binding.spnCompany.selectedItem as Company?
            val departmentOne = binding.spnDepartment.selectedItem as Department?
            val expenseTypeOne = binding.spnExpense.selectedItem as ExpenseType?
            val totalAmountOne = binding.edtTotalAmount.text
            val taxCodeOne = binding.edtTaxCode.text

            if(companyOne?.id.isNullOrEmpty() || departmentOne?.id.isNullOrEmpty()
                || expenseTypeOne?.expenseCodeID.isNullOrEmpty() || totalAmountOne.isNullOrEmpty()
                || taxCodeOne.isNullOrEmpty()) {
                isValid = false

                return isValid
            }

            val splitOne = SplitClaimDetail(companyOne?.id!!, departmentOne?.id!!, expenseTypeOne?.expenseCodeID!!,
                totalAmountOne.toString(), taxCodeOne.toString())

            Log.d(
                TAG, "validateAllSplitDetails Split 0 : " +
                        "company: ${splitOne.companyNumber} " +
                        "department: ${splitOne.department} " +
                        "expense: ${splitOne.expenseType} " +
                        "totalAmount: ${splitOne.totalAmount} " +
                        "taxCode: ${splitOne.tax}"
            )

            claimRequest.split.add(splitOne)
        }
        else{
            try {
                val companyOne = binding.spnCompany.selectedItem as Company?
                val departmentOne = binding.spnDepartment.selectedItem as Department?
                val expenseTypeOne = binding.spnExpense.selectedItem as ExpenseType?
                val totalAmountOne = binding.edtTotalAmount.text
                val taxCodeOne = binding.edtTaxCode.text

                if(companyOne?.id.isNullOrEmpty() || departmentOne?.id.isNullOrEmpty()
                    || expenseTypeOne?.expenseCodeID.isNullOrEmpty() || totalAmountOne.isNullOrEmpty()
                    || taxCodeOne.isNullOrEmpty()) {
                    isValid = false

                    return isValid
                }

                val splitOne = SplitClaimDetail(companyOne?.id!!, departmentOne?.id!!, expenseTypeOne?.expenseCodeID!!,
                    totalAmountOne.toString(), taxCodeOne.toString())

                Log.d(
                    TAG, "validateAllSplitDetails Split 0 : " +
                            "company: ${splitOne.companyNumber} " +
                            "department: ${splitOne.department} " +
                            "expense: ${splitOne.expenseType} " +
                            "totalAmount: ${splitOne.totalAmount} " +
                            "taxCode: ${splitOne.tax}"
                )

                claimRequest.split.add(splitOne)

                for (i in 1..viewModel.splitCount) {
                    val company = ((binding.layoutSplitDetail.getChildAt(i).findViewById(R.id.spnCompany)
                            as Spinner?)?.selectedItem as Company?)
                    val department = ((binding.layoutSplitDetail.getChildAt(i).findViewById(R.id.spnDepartment)
                            as Spinner?)?.selectedItem as Department?)
                    val expenseType = ((binding.layoutSplitDetail.getChildAt(i).findViewById(R.id.spnExpense)
                            as Spinner?)?.selectedItem as ExpenseType?)
                    val totalAmount = (binding.layoutSplitDetail.getChildAt(i).findViewById(R.id.edtTotalAmount)
                            as EditText?)?.text
                    val taxCode = (binding.layoutSplitDetail.getChildAt(i).findViewById(R.id.edtTaxCode)
                            as EditText?)?.text

                    if(company?.id.isNullOrEmpty() || department?.id.isNullOrEmpty()
                        || expenseType?.expenseCodeID.isNullOrEmpty() || totalAmount.isNullOrEmpty()
                        || taxCode.isNullOrEmpty()) {
                        isValid = false
                        break
                    }

                    val split = SplitClaimDetail(company?.id!!, department?.id!!, expenseType?.expenseCodeID!!,
                        totalAmount.toString(), taxCode.toString())

                    claimRequest.split.add(split)
                    Log.d(
                        TAG, "validateAllSplitDetails Split $i : " +
                                "company: ${split.companyNumber} " +
                                "department: ${split.department} " +
                                "expense: ${split.expenseType} " +
                                "totalAmount: ${split.totalAmount} " +
                                "taxCode: ${split.tax}"
                    )
                }
            }
            catch (e: Exception){
                Log.e(TAG, "validateAllSplitDetails: ${e.message}")
                isValid = false
            }
        }

        return isValid
    }

    private fun setResponse(commonResponse: CommonResponse) {
        binding.mProgressBars.visibility = View.GONE
        binding.btnSubmit.visibility = View.VISIBLE
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
        if(commonResponse.success) {
//            (contextActivity as? MainActivity)?.backButtonPressed()
            (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }

    private fun onCreateClaimFailed() {
        binding.btnSubmit.isEnabled = true
        Toast.makeText(contextActivity, getString(R.string.please_enter_all_mandatory_fields), Toast.LENGTH_SHORT).show()
    }
}