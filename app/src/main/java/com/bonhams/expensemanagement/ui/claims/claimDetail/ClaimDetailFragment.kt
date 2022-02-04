package com.bonhams.expensemanagement.ui.claims.claimDetail

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.FragmentClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.claimedit.EditClaimFragment
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimDetailsFragment
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils
import java.net.URL


class ClaimDetailFragment() : Fragment(), RecylerCallback {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var claimDetail: ClaimDetail
    private lateinit var viewModel: ClaimDetailViewModel
    private lateinit var binding: FragmentClaimDetailBinding
    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var splitedClaimDetails: ClaimDetailsResponse
    var isApproved=true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_claim_detail, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setupAttachmentRecyclerView()
        setupView()
        setClickListner()
        setObserver()
        getDetails()

        return view
    }

    override fun onResume() {
        super.onResume()

    }
    fun setClaimDetails(detail: ClaimDetail?){
        detail?.let {
            claimDetail = it
        }
    }
    fun setObserver(){
        mainViewModel.isRefresh?.observe(viewLifecycleOwner, {
           if(it){
               getDetails()
               mainViewModel.isRefresh?.value=false
           }
        })
    }

    private fun setupView(){
        try {
            if (this::claimDetail.isInitialized) {
                binding.tvTitle.text = claimDetail.title
                binding.tvMerchantName.text = claimDetail.merchant
                binding.tvExpenseGroup.text = claimDetail.expenseGroupName
                binding.tvAuction.text = claimDetail.auction.toString()
                binding.tvExpenseType.text = claimDetail.expenseTypeName
                binding.tvCompanyNumber.text = claimDetail.companyName
                binding.tvDepartment.text = claimDetail.department
                binding.tvLedgerId.text = claimDetail.merchant
                binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                    claimDetail.date_of_receipt,
                    Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                )
                binding.tvCurrency.text = claimDetail.currencyTypeName
                binding.tvTotalAmount.setText(claimDetail.currencySymbol+" "+String.format("%.2f", claimDetail.totalAmount.toDouble()))
               // binding.tvTotalAmount.setText(claimDetail.currencySymbol+" "+claimDetail.totalAmount)
                binding.tvTax.setText(claimDetail.currencySymbol+" "+String.format("%.2f", claimDetail.tax.toDouble()))
              //  binding.tvTax.setText(claimDetail.currencySymbol+" "+claimDetail.tax)
                binding.tvNetAmount.setText(claimDetail.currencySymbol+" "+String.format("%.2f", claimDetail.netAmount.toDouble()))
               // binding.tvNetAmount.setText(claimDetail.currencySymbol+" "+claimDetail.netAmount)
                binding.tvTaxCode.text = claimDetail.tax_code

                 println("claimDetail.rm_updation_date"+claimDetail.rm_updation_date)
                when (claimDetail.reportingMStatus) {
                    Constants.STATUS_PENDING -> {
                        binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextDarkGray))

                      binding.ivRMReminder.visibility=View.VISIBLE
                    }
                    Constants.STATUS_APPROVED -> {
                        isApproved=false

                        binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorGreen))
                        binding.tvRMStatusDate.visibility=View.VISIBLE
                        binding.tvRMStatusDate.text = Utils.getFormattedDate(
                            claimDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )

                    }
                    else -> {
                        binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRed))
                        binding.tvRMStatusDate.visibility=View.VISIBLE
                        binding.tvRMStatusDate.text = Utils.getFormattedDate(
                            claimDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )

                    }
                }
                when (claimDetail.financeMStatus) {
                    Constants.STATUS_PENDING -> {
                        binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextDarkGray))
                        binding.ivFMReminder.visibility=View.VISIBLE


                    }
                    Constants.STATUS_APPROVED -> {
                        isApproved=false
                        binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorGreen))
                        binding.tvFMStatusDate.visibility=View.VISIBLE

                        binding.tvFMStatusDate.text = Utils.getFormattedDate(
                            claimDetail.fm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )

                    }
                    else -> {
                        binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRed))
                        binding.tvFMStatusDate.visibility=View.VISIBLE
                        binding.tvFMStatusDate.text = Utils.getFormattedDate(
                            claimDetail.fm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )
                    }
                }

                binding.tvRMStatus.text = claimDetail.reportingMStatus
                binding.tvFMStatus.text = claimDetail.financeMStatus
                binding.tvDescription.text = claimDetail.description

                if (!claimDetail.attachments.isNullOrEmpty() && claimDetail.attachments.trim().isNotEmpty()) {

                    val attachment=claimDetail.attachments.split(",")
                    attachment.forEach {
                        viewModel.attachmentsList.add(it)
                    }

                }
            }
            refreshAttachments()
        }
        catch (error : Exception){
            Log.e(TAG, "setupView: ${error.message}")
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ClaimDetailViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ClaimDetailViewModel::class.java)

        mainViewModel.appbarMenuClick?.observe(viewLifecycleOwner, {
            showPopupMenu(it)
        })
    }
    private fun setClickListner() {
        binding.ivRMReminder.setOnClickListener {
            showReminderAlert("Are you sure you want to send reminder to the Reporting  Manager?","RM")

        }
        binding.ivFMReminder.setOnClickListener {
            showReminderAlert("Are you sure you want to send reminder to the Finance Manager?","FD")


        }
        binding.btnSplit.setOnClickListener {
            val fragment = SplitClaimDetailsFragment()
            fragment.setClaimRequestDetail(splitedClaimDetails)
            fragment.setEditable(isApproved)
            (contextActivity as? MainActivity)?.addFragment(fragment)

        }
    }

    private fun setupAttachmentRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttachments.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"detalis",this)
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun refreshAttachments(){
        if(viewModel.attachmentsList.size > 0){
            binding.tvAttachments.visibility = View.VISIBLE
            binding.rvAttachments.visibility = View.VISIBLE
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            binding.rvAttachments.visibility = View.GONE
            binding.tvAttachments.visibility = View.INVISIBLE
        }
    }
    private fun getDetails() {
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", claimDetail.id.toInt())
        }
        viewModel.getDetails(jsonObject,claimDetail.id).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                splitedClaimDetails=response
                                if(splitedClaimDetails.splitedClaim?.isEmpty() == true){
                                    binding.btnSplit.visibility=View.GONE
                                }else{
                                    binding.lnSplitAmount.visibility=View.GONE
                                    var splittotalamount: Double = 0.0
                                    splitedClaimDetails.splitedClaim?.forEach {
                                        splittotalamount +=it.totalAmount.toDouble()

                                    }
                                  /*  try {
                                        binding.tvSplitAmount.setText(claimDetail.currencySymbol+" "+splittotalamount)
                                    } catch (e: Exception) {
                                        binding.tvSplitAmount.setText(splittotalamount.toString())

                                    }*/


                                }
                                try {
                                    binding.tvNetAmount.setText(claimDetail.currencySymbol+" "+String.format("%.2f", splitedClaimDetails.main_claim?.netAmount?.toDouble()))
                                 //   binding.tvNetAmount.setText(claimDetail.currencySymbol+" "+splitedClaimDetails.main_claim?.netAmount)

                                } catch (e: Exception) {
                                    binding.tvNetAmount.setText(splitedClaimDetails.main_claim?.netAmount)
                                   // binding.tvNetAmount.setText(splitedClaimDetails.main_claim?.netAmount.toString())


                                }

                                //setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteClaim: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }
    private fun deleteClaim(){
        viewModel.deleteClaim(viewModel.getDeleteClaimRequest(claimDetail)).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "deleteClaim: ${resource.status}")
                                mainViewModel.isClaimListRefresh?.value=true
                                setResponse(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteClaim: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }
    private fun sendReminder(userType:String){
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", claimDetail.id.toInt())
            it.addProperty("for_user", userType)
        }
        viewModel.sendReminder(jsonObject).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "deleteClaim: ${resource.status}")
                                setResponseForReminder(response)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.e(TAG, "deleteClaim: ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(contextActivity, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }
    private fun setResponse(commonResponse: CommonResponse){
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
        if(commonResponse.success){
            (contextActivity as? MainActivity)?.clearFragmentBackstack()
        }
    }
    private fun setResponseForReminder(commonResponse: CommonResponse){
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()

    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(contextActivity, view)
        popup.inflate(R.menu.claims_menu)
        println("claimDetail.reportingMStatus"+claimDetail.reportingMStatus)
        println("claimDetail.financeMStatus"+claimDetail.financeMStatus)
        if(claimDetail.reportingMStatus==Constants.STATUS_PENDING||claimDetail.reportingMStatus==Constants.STATUS_REJECTED&&claimDetail.financeMStatus==Constants.STATUS_PENDING||claimDetail.financeMStatus==Constants.STATUS_REJECTED){

        }else{
            popup.menu.findItem(R.id.action_edit).isVisible = false

        }
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_copy -> {
                    val fragment = NewClaimFragment()
                    fragment.setClaimDetails(claimDetail)
//                    fragment.setRefreshPageListener(this)
                    (contextActivity as? MainActivity)?.addFragment(fragment)
                }
                R.id.action_edit -> {
                    val fragment = EditClaimFragment()
                    fragment.setClaimDetails(claimDetail)
//                    fragment.setRefreshPageListener(this)
                    (contextActivity as? MainActivity)?.addFragment(fragment)
                }
                R.id.action_delete -> {
                    showDeleteClaimAlert()
                }
            }
            true
        })

        popup.show()
    }

    private fun showDeleteClaimAlert(){
        contextActivity?.let { activity ->
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_alert_dialog)
            val title = dialog.findViewById(R.id.txtTitle) as TextView
            val body = dialog.findViewById(R.id.txtDescription) as TextView
            val input = dialog.findViewById(R.id.edtDescription) as EditText
            val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
            val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

            input.visibility = View.GONE
            title.text = resources.getString(R.string.delete_claim)
            body.text = resources.getString(R.string.are_you_sure_you_want_to_delete_claim)
            yesBtn.text = resources.getString(R.string.delete)
            noBtn.text = resources.getString(R.string.cancel)

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    deleteClaim()
                else
                    Toast.makeText(activity, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
            }
        }
    private fun showReminderAlert(message:String,userType:String){
        contextActivity?.let { activity ->
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.reminder_alert_dialog)
            val title = dialog.findViewById(R.id.txtTitle) as TextView
            val body = dialog.findViewById(R.id.txtDescription) as TextView
            val input = dialog.findViewById(R.id.edtDescription) as EditText
            val yesBtn = dialog.findViewById(R.id.btnPositive) as Button
            val noBtn = dialog.findViewById(R.id.btnNegative) as TextView

            input.visibility = View.GONE
            title.text = resources.getString(R.string.reminder_claim)
            body.text = message
            yesBtn.text = "Yes"
           // noBtn.text = resources.getString(R.string.cancel)

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    sendReminder(userType)
                else
                    Toast.makeText(activity, getString(R.string.check_internet_msg), Toast.LENGTH_SHORT).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }
    private fun showImagePopup(imageUrl:String) {
        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_popup_dialog)

        val image = dialog.findViewById(R.id.itemImage) as ImageView
        Glide.with(requireContext())
            .load(imageUrl)
            .dontAnimate()
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .placeholder(R.drawable.mountains)
                    .error(R.drawable.mountains)
            )
            .placeholder(R.drawable.mountains)
            .error(R.drawable.mountains)
            .into(image)


        dialog.show()
        val noBtn = dialog.findViewById(R.id.lnClose) as LinearLayout
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
    }
    override fun callback(action: String, data: Any, postion: Int) {
        if (action == "show") {
            showImagePopup(data as String)
        }
    }

}