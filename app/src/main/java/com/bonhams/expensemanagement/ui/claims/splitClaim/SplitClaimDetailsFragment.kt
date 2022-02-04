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
import com.bonhams.expensemanagement.adapters.*
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.NewClaimRequest
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.data.services.responses.SplitedClaim
import com.bonhams.expensemanagement.databinding.FragmentSplitClaimBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.ClaimDetailViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModel
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimViewModelFactory
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimDetalisActivity
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


class SplitClaimDetailsFragment() : Fragment() , RecylerCallback {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null

    private lateinit var binding: FragmentSplitClaimBinding
    private lateinit var viewModel: SplitClaimViewModel
    private lateinit var newClaimViewModel: NewClaimViewModel
    private lateinit var splitedClaimlist: List<SplitedClaim>
    private lateinit var splitedClaimDetails: ClaimDetailsResponse
    private lateinit var splitAdapter: SplitDetailsAdapterReadOnly
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
                    println("Splited id array$idOfSplitList")
                    println("Splited amount array$valueOfSplitList")
                    println("Splited net amount $totalCalculateamount")
                    println("Splited main_claim_id ${splitedClaimDetails.main_claim?.id}")

                     val jsonArrayId=JsonArray()
                     val jsonArrayValue=JsonArray()
                    idOfSplitList.forEach {
                        jsonArrayId.add(it)
                    }
                    valueOfSplitList.forEach {
                        jsonArrayValue.add(it)
                    }
                    val jsonObject = JsonObject().also {
                        it.add("split_ids",jsonArrayId)
                        it.add("split_total_amt",jsonArrayValue)
                        it.addProperty("main_net_amt", netAmount)
                        it.addProperty("main_claim_id", splitedClaimDetails.main_claim?.id)
                    }
                    editSplit(jsonObject)
                }

            }
        })
    }

    private fun setupView(){
        binding.layoutAddSplit.visibility=View.GONE
        binding.btnSubmit.visibility=View.GONE
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
        netAmount=splitedClaimDetails.main_claim?.netAmount.toString().toDouble()
       val  totalAmountWithTax=splitedClaimDetails.main_claim?.totalAmount.toString().toDouble()
       val  taxAmount=splitedClaimDetails.main_claim?.tax.toString().toDouble()
        totalAmount=totalAmountWithTax-taxAmount
       // binding.tvTotalAmount.text = "$ "+totalAmount
      // binding.tvTotalAmount.setText(netAmount.toString())

      // binding.tvTotalAmount.setText(remaningAmount.toString())
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
        splitAdapter = SplitDetailsAdapterReadOnly(isApproved,currencyCode,currencySymbol,splitItmlist as MutableList<SplitClaimItem?>,requireActivity(),this)
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


    private fun editSplit(jsonObject:JsonObject) {

        viewModel.updateSplit(jsonObject).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                               (contextActivity as? MainActivity)?.backButtonPressed()
                               (contextActivity as? MainActivity)?.clearFragmentBackstack()
                                mainViewModel.isRefresh?.value=true
                                //setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteClaim: ${it.message}")
                        it.message?.let { it1 ->
                            Toast.makeText(
                                contextActivity,
                                it1,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    Status.LOADING -> {

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
                itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0"
            )
            val intent = Intent(requireContext(), SplitClaimDetalisActivity::class.java)
            intent.putExtra("SplitItem", splitItem)
            intent.putExtra("currencyCode", currencyCode)
            intent.putExtra("currencySymbol", currencySymbol)
            intent.putExtra("postion", postion)
            intent.putExtra("groupId", splitedClaimDetails.main_claim?.expenseGroupID)
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
            remaningAmount=splitedSplitAmount-totalSplitAmount
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