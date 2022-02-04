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
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.adapters.SplitAdapter
import com.bonhams.expensemanagement.adapters.SplitDetailsAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.SplitedClaim
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.io.File


class EditSplitClaimDetailsFragment() : Fragment() , RecylerCallback {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null

    private lateinit var binding: FragmentSplitClaimBinding
    private lateinit var viewModel: SplitClaimViewModel
    private lateinit var newClaimViewModel: NewClaimViewModel
    private lateinit var splitedClaimlist: List<SplitedClaim>
    private lateinit var splitedClaimDetails: ClaimDetailsResponse
    private lateinit var objectRequest: JsonObject
    private lateinit var dropdownResponse: DropdownResponse
    private lateinit var splitAdapter: SplitDetailsAdapter
    private var currencyCode: String = ""
    private var currencySymbol: String = ""
    private var idOfSplitList: MutableList<String> = mutableListOf()
    private var valueOfSplitList: MutableList<String> = mutableListOf()
    private val mainViewModel: MainViewModel by activityViewModels()
    var splitedSplitAmount=0.0
    var remaningAmount=0.0
    var isApproved=true


    companion object {
       public var splitItmlist: MutableList<SplitClaimItem> = mutableListOf()
       public var totalAmount: Double=0.00
       public var netAmount: Double=0.00

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
       // setupSpinners()
        setupView()
        return view
    }

    override fun onResume() {
        super.onResume()
        splitAdapter.notifyDataSetChanged()

    }
    fun setClaimRequestDetail(request: ClaimDetailsResponse){


        try {
            splitedClaimDetails = request
            splitedClaimlist= request.splitedClaim!!
            currencyCode= splitedClaimDetails.main_claim?.currencyTypeName.toString()
            currencySymbol= splitedClaimDetails.main_claim?.currencySymbol.toString()
        } catch (e: Exception) {
        }


    }
    fun setClaimRequestDetailJson(request: JsonObject){
        try {
            objectRequest = request

        } catch (e: Exception) {
        }


    }
    fun setdropdownResponse(dropDownResponse: DropdownResponse){
        try {
            dropdownResponse = dropDownResponse

        } catch (e: Exception) {
        }


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
            val intent = Intent(requireActivity(), SplitClaimActivity::class.java)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

           // createNewSplitLayout()
        })

        binding.btnSubmit.setOnClickListener(View.OnClickListener {
            contextActivity?.let {
               // splitAdapter.notifyDataSetChanged()
                var splittotalamount: Double = 0.00
                splitItmlist.forEachIndexed { index, splitClaimItem ->
                    splittotalamount +=splitClaimItem.totalAmount.toDouble()
                    idOfSplitList.add(splitClaimItem.split_id)
                    valueOfSplitList.add(splitClaimItem.totalAmount)
                }
               // if(totalAmount<splittotalamount){
                if(remaningAmount>0||remaningAmount<0){
                    Toast.makeText(requireContext(), " Net amount should be equal to Splits amount", Toast.LENGTH_SHORT)
                        .show()
                    return@OnClickListener
                }else{
                    val totalCalculateamount=totalAmount-splittotalamount
                   // binding.tvTotalAmount.setText(totalCalculateamount.toString())
                    println("companyList size ${newClaimViewModel.companyList.size}")
                    println("departmentList size ${newClaimViewModel.departmentList.size}")
                    println("expenseTypeList size ${newClaimViewModel.expenseTypeList.size}")



                    val jsonArraySplitId= JsonArray()
                    val jsonArraySplitData= JsonArray()
                    splitItmlist.forEachIndexed { index, splitClaimItem ->
                        println("splitClaimItem ${splitClaimItem}")

                        jsonArraySplitId.add(splitClaimItem.split_id)
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
                        data.addProperty("expense_type_id",splitClaimItem.expenseType  )
                        data.addProperty("company_id", companyId)
                        data.addProperty("department_id", departmentId )
                        data.addProperty("amount", splitClaimItem.totalAmount)
                        data.addProperty("tax_code",mtaxcodeId)
                        data.addProperty("auction",splitClaimItem.auctionSales )
                        data.addProperty("expense_code", expenceCode )
                        jsonArraySplitData.add(data)
                    }
                    objectRequest.add("split_ids",jsonArraySplitId)
                    objectRequest.add("split_data",jsonArraySplitData)


                    callApiEditClaim(objectRequest)
                }

            }
        })
    }

    private fun setupView(){


        newClaimViewModel.expenseTypeListExpenseGroup = dropdownResponse.expenseType
        newClaimViewModel.departmentListCompany = dropdownResponse.departmentList
        newClaimViewModel.currencyList  = dropdownResponse.currencyType as MutableList<Currency>
        newClaimViewModel.carTypeList  = dropdownResponse.carType
        newClaimViewModel.statusTypeList  = dropdownResponse.statusType
        newClaimViewModel.mileageTypeList  = dropdownResponse.mileageType
        newClaimViewModel.companyList  = dropdownResponse.companyList
        newClaimViewModel.taxList  = dropdownResponse.tax

        binding.layoutAddSplit.visibility=View.GONE
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
                "0", "0", it.expenseTypeID,
                it.totalAmount,
                it.tax_code, it.tax.toDouble(), it.companyNumber ?:"", it.department ?:"",
                it.expenseTypeName,auction,expenseCode,
                expenseCode,it.tax_code,it.split_id
            )
            println("item :$splitOne")

            splitItmlist.add(splitOne)
        }

        var totalSplitAmount=0.00
        splitItmlist.forEach {
            totalSplitAmount += it.totalAmount.toDouble()
        }
        splitedSplitAmount=totalSplitAmount
        binding.totalSplitAmount.text = currencySymbol+" "+String.format("%.2f",totalSplitAmount)
        remaningAmount=splitedSplitAmount-totalSplitAmount
        splitAdapter.notifyDataSetChanged()

        //  binding.tvMerchantName.text = claimRequest.expenseCode
     //   binding.tvDate.text = claimRequest.dateSubmitted
     //   binding.tvUserName.text = AppPreferences.fullName

        binding.tvTotalAmountCurrency.text = currencySymbol
       // binding.tvTotalAmount.setLocale(currencyCode)
      //  netAmount=splitedClaimDetails.main_claim?.netAmount.toString().toDouble()
       //val  totalAmountWithTax=splitedClaimDetails.main_claim?.totalAmount.toString().toDouble()
        //val  taxAmount=splitedClaimDetails.main_claim?.tax.toString().toDouble()
        netAmount=objectRequest.get("netAmount").asDouble

        val  totalAmountWithTax=objectRequest.get("totalAmount").asDouble
        val  taxAmount=objectRequest.get("tax").asDouble
        println("totalAmountWithTax:"+totalAmountWithTax)
        println("taxAmount:"+taxAmount)


        totalAmount=totalAmountWithTax-taxAmount
       // binding.tvTotalAmount.text = "$ "+totalAmount
      // binding.tvTotalAmount.setText(netAmount.toString())

      // binding.tvTotalAmount.setText(remaningAmount.toString())

        remaningAmount=totalAmount-totalSplitAmount
        //binding.tvTotalAmount.setText(remaningAmount.toString())
        binding.tvTotalAmount.setText(String.format("%.2f", remaningAmount))

        binding.tvTotalAmount.setText(String.format("%.2f", remaningAmount))

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
        splitAdapter = SplitDetailsAdapter(isApproved,currencyCode,currencySymbol,splitItmlist as MutableList<SplitClaimItem?>,requireActivity(),this)
        binding.rvsplit.adapter = splitAdapter
    }


    private fun callApiEditClaim(newClaimRequest: JsonObject){
        viewModel.editClaim(newClaimRequest).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                binding.mProgressBars.visibility = View.GONE
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity(). finish()
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




    override fun callback(action: String, data: Any, postion: Int) {

        if(action=="details"){
            val itemData=data as SplitClaimItem
            val splitItem = SplitClaimItem(
                itemData?.companyNumber?:"0", itemData?.companyCode?:"0", itemData?.department?:"0", itemData?.expenseType?:"0",
                itemData?.totalAmount?:"0", itemData?.taxcode?:"0",itemData?.tax?:0.0,itemData?.compnyName?:"0",itemData?.departmentName?:"0",
                itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0",itemData.expenseCodeID,itemData.taxCodeValue,itemData.split_id
            )
            println("splitItem :$splitItem")

            val intent = Intent(requireContext(), EditSplitClaimDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra("SplitItem", splitItem)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            intent.putExtra("postion", postion)
            intent.putExtra("groupId", objectRequest.get("expenseGroup").asString)
           // intent.putExtra("taxAmount", binding.tvTaxAmount.text.toString().toDouble())
            requireActivity()?.startActivity(intent)
        }else if(action == "update"){

            val amount = data as Double
            val item= splitItmlist[postion]
            item.totalAmount= amount.toString()

            splitItmlist.removeAt(postion)
            splitItmlist.add(postion,item)
             var totalSplitAmount=0.0
            splitItmlist.forEach {
                totalSplitAmount += it.totalAmount.toDouble()
                println(" splited amount detalis :"+totalSplitAmount)
            }
            remaningAmount= totalAmount-totalSplitAmount
            //binding.tvTotalAmount.setText(remaningAmount.toString())
            binding.tvTotalAmount.setText(String.format("%.2f", remaningAmount))

            binding.totalSplitAmount.setText(currencySymbol+" "+totalSplitAmount)
        }else {
            val amount = data as Double
            totalAmount += amount
            // binding.tvTotalAmount.text = "$ "+totalAmount
           // binding.tvTotalAmount.setText(totalAmount.toString())
            binding.tvTotalAmount.setText(String.format("%.2f", totalAmount))

        }

    }
}