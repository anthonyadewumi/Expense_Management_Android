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
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.SplitedClaim
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimBinding
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimEditBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.*
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.File


class SplitClaimFragmentEdit() : Fragment() , RecylerCallback {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var splitedClaimlist: List<SplitedClaim>

    private lateinit var binding: FragmentSplitClaimEditBinding
    private lateinit var viewModel: SplitClaimViewModel
    private lateinit var newClaimViewModel: NewClaimViewModel
    private lateinit var claimRequest: NewClaimRequest
    private lateinit var mSplitClaimItem: SplitClaimItem
    private lateinit var dropdownResponse: DropdownResponse

    private lateinit var splitAdapter: SplitAdapter
    private var currencyCode: String = ""
    private var currencySymbol: String = ""
    var splitedSplitAmount=0.0
    private lateinit var objectRequest: JsonObject
    private  var isRMEdit=false
    var isApproved=true
    val jsonArraySplitId= JsonArray()
    var groupname=""

    companion object {
       public var splitItmlist: MutableList<SplitClaimItem> = mutableListOf()
       public var totalAmount: Double=0.00
       public var netAmount: Double=0.00
       public var taxAmount: Double=0.00
       public var remaningAmount: Double=0.00

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_split_claim_edit, container, false)
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

            var splittotalamountDefault: Double = 0.00
            var splitTaxamountDefault: Double = 0.00


            splitItmlist.forEachIndexed { index, splitClaimItem ->
                if(index!=0) {
                  splittotalamountDefault += splitClaimItem.totalAmount.toDouble()
                  splitTaxamountDefault += splitClaimItem.tax
              }

            }


            val totalCalculateamountDefault = totalAmount - splittotalamountDefault
            val totalCalculatedTaxdefault = taxAmount - splitTaxamountDefault

            splitItmlist[0].totalAmount=totalCalculateamountDefault.toString()
            splitItmlist[0].tax=totalCalculatedTaxdefault

            splitItmlist.forEachIndexed { index, splitClaimItem ->
                println("splitClaimItem: ${splitClaimItem}")
                println("totalAmount: ${splitClaimItem.totalAmount}")
                println("taxamount: ${splitClaimItem.tax}")

                splittotalamount += splitClaimItem.totalAmount.toDouble()
                splitTaxamount += splitClaimItem.tax

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
                binding.tvTaxAmount.setText(String.format("%.2f",totalCalculatedTax.toString().toDouble()))
            } catch (e: Exception) {
            }

            var amount=0.00
            splitItmlist.forEachIndexed { index, splitClaimItem ->
                amount += splitClaimItem.totalAmount.toDouble()
            }
            binding.layoutTotalSplit.visibility=View.VISIBLE
            binding.totalSplitAmount.text = currencySymbol+" "+String.format("%.2f",amount)

            splitAdapter.notifyDataSetChanged()
        }
        splitAdapter.notifyDataSetChanged()
        if(splitItmlist[0].totalAmount=="0.0"){
            binding.layoutAddSplit.visibility=View.GONE

        }else{
            binding.layoutAddSplit.visibility=View.VISIBLE

        }
       // binding.tvTotalAmount.setText(totalAmount.toString())

    }
    fun setClaimRequestDetail(request: NewClaimRequest){
        claimRequest = request
        System.out.println("claimRequest totalAmount : ${claimRequest.totalAmount}")
        System.out.println("claimRequest netAmount : ${claimRequest.netAmount}")


    }
    fun setClaimRequestDetail(request: ClaimDetailsResponse){
        try {
            splitedClaimlist= request.splitedClaim!!
        } catch (e: Exception) {
        }

    }
    fun setdropdownResponse(dropDownResponse: DropdownResponse){
        try {
            dropdownResponse = dropDownResponse

        } catch (e: Exception) {
        }


    }
    fun setClaimRequestDetailJson(request: JsonObject){
        try {
            objectRequest = request

        } catch (e: Exception) {
        }


    }
    fun setSplitRequestDetail(item: SplitClaimItem){
        mSplitClaimItem = item



    }
    fun setEditModeRM(isedit: Boolean){
        try {
            isRMEdit = isedit

        } catch (e: Exception) {
        }


    }
    fun setGroupName(mgroupname: String){
        groupname = mgroupname



    }
    fun setEditable(editable: Boolean){


        try {
            isApproved=editable
        } catch (e: Exception) {
        }


    }

    fun setCurrency(code: String,symbol:String){
        currencyCode=code
        currencySymbol=symbol

    }

    private fun setClickListeners(){
        binding.layoutAddSplit.setOnClickListener(View.OnClickListener {

           println("total amount in this "+ splitItmlist[0].totalAmount)
            val intent = Intent(requireActivity(), SplitClaimActivity2::class.java)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            intent.putExtra("department", claimRequest.department)
            intent.putExtra("company", claimRequest.companyNumber)
            intent.putExtra("groupId", claimRequest.expenseGroup)
            intent.putExtra("groupName", groupname)

            intent.putExtra("expenceType", claimRequest.expenseType)
            intent.putExtra("taxAmount", splitItmlist[0].tax)
            intent.putExtra("totalAmount", splitItmlist[0].totalAmount)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

           // createNewSplitLayout()
        })

        binding.tvTotalAmount.doAfterTextChanged {
            if(binding.tvTotalAmount.text.isNotEmpty()){
                if(binding.tvTotalAmount.text.toString()=="0.00"){
                    binding.layoutAddSplit.visibility=View.VISIBLE
                }else{
                    binding.layoutAddSplit.visibility=View.VISIBLE

                }
            }
        }
/*
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
        })*/

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
                var splittotalamount: Double = 0.00

                if(splitItmlist[0].totalAmount.toDouble()<0){
                    Toast.makeText(requireContext(), " Net amount should be equal to Splits amount", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }

                if(remaningAmount>0||remaningAmount<0){
                    Toast.makeText(requireContext(), " Net amount should be equal to Splits amount", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }else{
                    val totalCalculateamount= totalAmount -splittotalamount
                    // binding.tvTotalAmount.setText(totalCalculateamount.toString())
                    println("companyList size ${newClaimViewModel.companyList.size}")
                    println("departmentList size ${newClaimViewModel.departmentList.size}")
                    println("expenseTypeList size ${newClaimViewModel.expenseTypeList.size}")
                    val jsonArraySplitData= JsonArray()
                  splitItmlist.forEachIndexed { index, splitClaimItem ->
                        val data= JsonObject()
                        var expenceCode= ""
                        var mtaxcodeId= ""
                        newClaimViewModel.expenseTypeListExpenseGroup.forEach {
                            if(it.id==splitClaimItem.expenseType)
                                expenceCode=it.expenseCodeID
                        }
                        newClaimViewModel.taxList.forEach {
                            if(it.tax_code==splitClaimItem.taxCodeValue)
                                mtaxcodeId= it.id.toString()
                        }
                        var companyId= ""
                        newClaimViewModel.companyList.forEach {
                            if(it.code==splitClaimItem.companyCode||it.code==splitClaimItem.compnyName)
                                companyId=it.id
                        }
                        var departmentId= ""
                        newClaimViewModel.departmentListCompany.forEach {
                            if(it.id==splitClaimItem.department||it.cost_code==splitClaimItem.departmentName)
                                departmentId=it.id
                        }

                        data.addProperty("expenseType",splitClaimItem.expenseType)
                        data.addProperty("companyNumber", companyId)
                        data.addProperty("department", departmentId )
                        data.addProperty("totalAmount", splitClaimItem.totalAmount)
                        data.addProperty("taxCode",mtaxcodeId)
                        data.addProperty("tax",splitClaimItem.tax)
                        data.addProperty("auction",splitClaimItem.auctionSales )
                        data.addProperty("expenseCode", expenceCode )
                        jsonArraySplitData.add(data)
                    }
                   objectRequest.add("delete_id",jsonArraySplitId)
                    objectRequest.add("split_data",jsonArraySplitData)


                    callApiEditClaim(objectRequest)
                }

            }
        })



       }

    private fun setupView(){
//        binding.tvMerchantName.text = claimRequest.expenseCode
     //   binding.tvDate.text = claimRequest.dateSubmitted
       // splitItmlist.add(mSplitClaimItem)

        newClaimViewModel.expenseTypeListExpenseGroup = dropdownResponse.expenseType
        newClaimViewModel.departmentListCompany = dropdownResponse.departmentList
        newClaimViewModel.currencyList  = dropdownResponse.currencyType as MutableList<Currency>
        newClaimViewModel.carTypeList  = dropdownResponse.carType
        newClaimViewModel.statusTypeList  = dropdownResponse.statusType
        newClaimViewModel.mileageTypeList  = dropdownResponse.mileageType
        newClaimViewModel.companyList  = dropdownResponse.companyList
        newClaimViewModel.taxList  = dropdownResponse.tax
       splitItmlist.clear()
        splitedClaimlist.forEach {
            var auction=""
            var expenseCode=""
            if(it.auction!=null){
                auction =it.auction.toString()

            }
            if(it.expenseCode!=null){
                expenseCode =it.auction.toString()

            }
            val splitOne = SplitClaimItem(
                it.id ?:"0",
                it.companyNumber, it.department, it.expenseTypeID,
                it.totalAmount,
                it.tax_code, it.tax.toDouble(), it.companyNumber ?:"", it.department ?:"",
                it.expenseTypeName,auction,expenseCode,
                expenseCode,it.tax_code,it.split_id,it.expenseGroupID
            )
            println("item :$splitOne")

            splitItmlist.add(splitOne)
        }
        splitItmlist.forEachIndexed { index, splitClaimItem ->
            if (splitClaimItem.split_id.isNotEmpty()) {
                jsonArraySplitId.add(splitClaimItem.split_id)
            }

        }

        binding.tvTotalAmountCurrency.text = currencySymbol
        binding.tvTaxAmount.setText(currencySymbol)
        binding.tvTaxCurrency.text = currencySymbol
       // binding.tvTotalAmount.setLocale(currencyCode)
        netAmount=claimRequest.netAmount.toString().toDouble()
        taxAmount=claimRequest.tax.toString().toDouble()
        remaningAmount=netAmount
        //totalAmount=claimRequest.totalAmount.toString().toDouble()
        val  totalAmountWithTax=claimRequest?.totalAmount.toString().toDouble()
        taxAmount=claimRequest?.tax.toString().toDouble()
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
    private fun callApiEditClaim(newClaimRequest: JsonObject){
        viewModel.editClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                if(isRMEdit){
                                    requireActivity().finish()
                                }else {
                                    binding.mProgressBars.visibility = View.GONE
                                   val intent = Intent(requireActivity(), MainActivity::class.java)
                                   startActivity(intent)
                                    requireActivity().finish()
                                }
                                /*(contextActivity as? MainActivity)?.backButtonPressed()
                                (contextActivity as? MainActivity)?.clearFragmentBackstack()
                                mainViewModel.isRefresh?.value=true
                                activity?.getFragmentManager()?.popBackStackImmediate()*/
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        binding.mProgressBars.visibility = View.GONE
                        binding.btnSubmit.visibility = View.VISIBLE
                        Log.e(TAG, "editClaimObserver: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {
                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
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




    override fun callback(action: String, data: Any ,postion: Int) {
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
                val totalamount=(data as SplitClaimItem).totalAmount.toDouble()
                val taxamount=data.tax
                splitItmlist[0].totalAmount=(splitItmlist[0].totalAmount.toDouble() +totalamount).toString()
                splitItmlist[0].tax=splitItmlist[0].tax+taxamount
                splitItmlist.forEachIndexed { index, splitClaimItem ->
                    println("totalAmount: ${splitClaimItem.totalAmount}")

                    splittotalamount += splitClaimItem.totalAmount.toDouble()
                    splitTaxamount += splitClaimItem.tax

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
                itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0",itemData?.expenseCodeID?:"0",itemData?.taxCodeValue?:"0",itemData?.split_id?:"0",itemData?.expense_group_id?:"0"
            )
            if(postion==0){
                val intent = Intent(requireContext(), SplitClaimDetalisActivity::class.java)
                intent.putExtra("SplitItem", splitItem)
                intent.putExtra("currencyCode", currencyCode)
                intent.putExtra("currencySymbol", currencySymbol)
                intent.putExtra("postion", postion)
                intent.putExtra("groupId", itemData?.expense_group_id)
                // intent.putExtra("taxAmount", binding.tvTaxAmount.text.toString().toDouble())
                requireActivity()?.startActivity(intent)
            }else{
                val intent = Intent(requireContext(), EditSplitClaimActivityEdit::class.java)
           intent.putExtra("SplitItem", splitItem)
           intent.putExtra("currencyCode", currencyCode)
           intent.putExtra("currencySymbol", currencySymbol)
           intent.putExtra("postion", postion)
           intent.putExtra("groupId", claimRequest.expenseGroup)
           intent.putExtra("taxAmount", binding.tvTaxAmount.text.toString().toDouble())
                requireActivity().startActivity(intent)
            }




        }
    }
}