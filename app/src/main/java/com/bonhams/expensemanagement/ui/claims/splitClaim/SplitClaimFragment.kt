package com.bonhams.expensemanagement.ui.claims.splitClaim

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doAfterTextChanged
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
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.File


class SplitClaimFragment() : Fragment() , RecylerCallback {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null

    private lateinit var binding: FragmentSplitClaimBinding
    private lateinit var viewModel: SplitClaimViewModel
    private lateinit var newClaimViewModel: NewClaimViewModel
    private lateinit var claimRequest: NewClaimRequest
    private lateinit var mItem: SplitClaimItem
    private lateinit var splitAdapter: SplitAdapter
    private var currencyCode: String = ""
    private var currencySymbol: String = ""
    var splitedSplitAmount=0.0

    companion object {
       public var splitItmlist: MutableList<SplitClaimItem> = mutableListOf()
       public var totalAmount: Double=0.00
       public var netAmount: Double=0.00
       public var taxAmount: Double=0.00
       public var remaningAmount: Double=0.00
       public var remaningTaxAmount: Double=0.00

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
        splitItmlist.clear()

        setupViewModel()
        setClickListeners()
        setupSplitRecyclerView()
       // setupSpinners()
        setupView()
        return view
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()

    }
    override fun onResume() {
        super.onResume()
        if(splitItmlist.isNotEmpty()) {
            var splittotalamount: Double = 0.00
            var splitTaxamount: Double = 0.00

            splitItmlist.forEachIndexed { index, splitClaimItem ->
                println("totalAmount: ${splitClaimItem.totalAmount}")

                splittotalamount += splitClaimItem.totalAmount.toDouble()
                splitTaxamount += splitClaimItem.tax.toDouble()

            }
            if (totalAmount < splittotalamount) {
                Toast.makeText(requireContext(), "Net amount should be equal to Splits amount ", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val totalCalculateamount = totalAmount - splittotalamount
                remaningAmount=totalCalculateamount
                binding.tvTotalAmount.setText(String.format("%.2f",totalCalculateamount.toString().toDouble()))
            }

            try {
                val totalCalculatedTax = taxAmount - splitTaxamount
                remaningTaxAmount=totalCalculatedTax
                binding.tvTaxAmount.setText(String.format("%.2f",totalCalculatedTax.toString().toDouble()))
            } catch (e: Exception) {
            }

            splitAdapter.notifyDataSetChanged()
        }
        splitAdapter.notifyDataSetChanged()
       // binding.tvTotalAmount.setText(totalAmount.toString())

    }
    fun setClaimRequestDetail(request: NewClaimRequest){
        claimRequest = request
        System.out.println("claimRequest totalAmount : ${claimRequest.totalAmount}")
        System.out.println("claimRequest netAmount : ${claimRequest.netAmount}")


    }
    fun setSplitRequestDetail(item: SplitClaimItem){
        mItem = item



    }
    fun setCurrency(code: String,symbol:String){
        currencyCode=code
        currencySymbol=symbol

    }

    private fun setClickListeners(){
        binding.layoutAddSplit.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireActivity(), SplitClaimActivity::class.java)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            intent.putExtra("groupId", claimRequest.expenseGroup)
            intent.putExtra("taxAmount", binding.tvTaxAmount.text.toString().toDouble())
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

           // createNewSplitLayout()
        })

        binding.tvTotalAmount.doAfterTextChanged {
            if(binding.tvTotalAmount.text.isNotEmpty()){
                if(binding.tvTotalAmount.text.toString()=="0.00"){
                    binding.layoutAddSplit.visibility=View.GONE
                }else{
                    binding.layoutAddSplit.visibility=View.VISIBLE

                }
            }
        }

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                if (NoInternetUtils.isConnectedToInternet(it))
                    if(binding.tvTotalAmount.text.toString().toDouble()>0){
                        Toast.makeText(it, "Net amount should be equal to Splits amount", Toast.LENGTH_LONG).show()
                      return@OnClickListener
                    }else{
                        createNewClaim()

                    }
                else
                    Toast.makeText(it, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
        })
       }

    private fun setupView(){
//        binding.tvMerchantName.text = claimRequest.expenseCode
     //   binding.tvDate.text = claimRequest.dateSubmitted
        splitItmlist.add(mItem)

        binding.layoutTotalSplit.visibility=View.GONE

        binding.tvTotalAmountCurrency.setText(currencySymbol)
        binding.tvTaxAmount.setText(currencySymbol)
        binding.tvTaxCurrency.setText(currencySymbol)
       // binding.tvTotalAmount.setLocale(currencyCode)
        netAmount=claimRequest.netAmount.toString().toDouble()
        taxAmount=claimRequest.tax.toString().toDouble()
        remaningAmount=netAmount
        //totalAmount=claimRequest.totalAmount.toString().toDouble()
        val  totalAmountWithTax=claimRequest?.totalAmount.toString().toDouble()
        val  taxAmount=claimRequest?.tax.toString().toDouble()
        totalAmount =totalAmountWithTax-taxAmount
        splitedSplitAmount=totalAmount

        //binding.tvTotalAmount.text = "$ "+totalAmount
        binding.tvTotalAmount.setText(String.format("%.2f",netAmount.toString().toDouble()))
        binding.tvTaxAmount.setText(String.format("%.2f",taxAmount.toString().toDouble()))

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
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvsplit.layoutManager = linearLayoutManager
        splitAdapter = SplitAdapter(currencyCode,currencySymbol,splitItmlist as MutableList<SplitClaimItem?>,requireActivity(),this)
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
        println("chk split item size :"+splitItmlist.size)
        splitItmlist.forEach {
            println(" split item :"+it)
            var auction="0"
                if(it.auctionSales.isNotEmpty())
            {
                auction=it.auctionSales
            }
            var expenseCodeID="0"
                if(it.expenseCodeID.isNotEmpty())
            {
                expenseCodeID=it.expenseCodeID
            }
            val splitOne = SplitClaimDetail(it?.companyNumber!!, it?.department!!, it.expenseType!!,
                it.totalAmount, it.tax,it.taxcode.toInt(),auction,expenseCodeID)
            claimRequest.split.add(splitOne)

        }
        println("chk split item claimRequest :"+claimRequest.split.size)
       // claimRequest.netAmount= remaningAmount.toString()


        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("claim_type", "E")
        for (photoPath in claimRequest.attachments) {
            if (photoPath != null) {
                val images = File(photoPath)
                if (images.exists()) {
                    builder.addFormDataPart("claimImage", images.name, RequestBody.create(MultipartBody.FORM, images))
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
                                claimRequest.attachments=response.images
                                setCreateClaimObserver(claimRequest)

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





        /*  if(!validateAllSplitDetails()){
              onCreateClaimFailed()
              return
          }

          binding.btnSubmit.visibility = View.GONE
  */

       // setCreateClaimObserver(claimRequest)
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
                totalAmountOne.toString(), taxCodeOne.toString().toDouble())

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
                    totalAmountOne.toString(), taxCodeOne.toString().toDouble())

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
                        totalAmount.toString(), taxCode.toString().toDouble())

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

            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity(). finish()

//            (contextActivity as? MainActivity)?.backButtonPressed()
           // (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }

    private fun onCreateClaimFailed() {
        binding.btnSubmit.isEnabled = true
        Toast.makeText(contextActivity, getString(R.string.please_enter_all_mandatory_fields), Toast.LENGTH_SHORT).show()
    }




    override fun callback(action: String, data: Any, postion: Int) {

        if(action == "update"){

            /*val amount = data as Double
            val item= splitItmlist[postion]
            item.totalAmount= amount.toString()

            splitItmlist.removeAt(postion)
            splitItmlist.add(postion,item)
            var totalSplitAmount=0.0
            splitItmlist.forEach {
                println(" splited amount :"+it)

                totalSplitAmount += it.totalAmount.toDouble()
                println(" splited amount :"+totalSplitAmount)
            }
            remaningAmount=splitedSplitAmount-totalSplitAmount
            binding.tvTotalAmount.setText(String.format("%.2f", remaningAmount))
            binding.totalSplitAmount.text = currencySymbol+" "+totalSplitAmount*/
        }else if(action=="remove") {
            if (splitItmlist.isNotEmpty()) {
                var splittotalamount: Double = 0.0
                var splitTaxamount: Double = 0.0

                splitItmlist.forEachIndexed { index, splitClaimItem ->
                    println("totalAmount: ${splitClaimItem.totalAmount}")

                    splittotalamount += splitClaimItem.totalAmount.toDouble()
                    splitTaxamount += splitClaimItem.tax.toDouble()

                }
                if (totalAmount < splittotalamount) {
                    Toast.makeText(
                        requireContext(),
                        "Split amount should not be greater to The net amount below ",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val totalCalculateamount = totalAmount - splittotalamount
                    remaningAmount = totalCalculateamount
                    binding.tvTotalAmount.setText(String.format("%.2f",totalCalculateamount.toString().toDouble()))
                }

                splitAdapter.notifyDataSetChanged()
            }else{
                binding.tvTotalAmount.setText(String.format("%.2f",netAmount.toString().toDouble()))
                remaningAmount= netAmount
            }
        }else if(action=="details"){
            val itemData=data as SplitClaimItem
            val splitItem = SplitClaimItem(
                itemData?.companyNumber?:"0", itemData?.companyCode?:"0", itemData?.department?:"0", itemData?.expenseType?:"0",
                itemData?.totalAmount?:"0", itemData?.taxcode?:"0",itemData?.tax?:0.0,itemData?.compnyName?:"0",itemData?.departmentName?:"0",
                itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0"
            )
            val intent = Intent(requireContext(), EditSplitClaimActivity::class.java)
            intent.putExtra("SplitItem", splitItem)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            intent.putExtra("postion", postion)
            intent.putExtra("groupId", claimRequest.expenseGroup)
            intent.putExtra("taxAmount", binding.tvTaxAmount.text.toString().toDouble())
            requireActivity()?.startActivity(intent)
        }
    }
}