package com.bonhams.expensemanagement.ui.rmExpence

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.ExpencesDetailsListAdapter
import com.bonhams.expensemanagement.adapters.ExpencesListAdapter
import com.bonhams.expensemanagement.adapters.ImagePager2Adapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.ClaimDetailsResponse
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.MIleageDetailsResponse
import com.bonhams.expensemanagement.databinding.ActivityExpenceAcceptedBinding
import com.bonhams.expensemanagement.databinding.ActivityReportingMangerExpenceDetailsBinding
import com.bonhams.expensemanagement.databinding.ActivityRequestClaimDetailsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.claimDetail.claimedit.EditClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.utils.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class RequestClaimDetails : BaseActivity(), RecylerCallback {
    private lateinit var binding: ActivityRequestClaimDetailsBinding
    private lateinit var viewModel: ExpenceViewModel
    val attachmentList =  ArrayList<String>()
    var requestId=""
    var claimType=""
    private var requestid: String = "0"
    private lateinit var claimDetail: ClaimDetailsResponse
    private lateinit var mileageDetail: MIleageDetailsResponse
    var rmStatus=""
    var fmStatus=""
    var status=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_claim_details)
        binding.lifecycleOwner = this
        setupViewModel()
        setClickListeners()
    }

    override fun onResume() {
        super.onResume()
        setupView()

    }

    private fun setupView() {


        val details = intent.getSerializableExtra("Details") as? ExpenceDetailsData
        requestid = intent.getStringExtra("employeeId").toString()
        claimType= details?.claimType.toString()
        println("details :$details")
        requestId=details?.requestId.toString()
        getDetails(requestId)
        if(details?.claimType=="Expense")
        { getDetails(requestId)


        }else{
            getMileageDetails(requestId)
        }


        val attachment=details?.attachments?.split(",")
        attachment?.forEach {
            attachmentList.add(it)
        }
        val adapter = ImagePager2Adapter(this, this,attachmentList)
        binding.viewPager2.adapter = adapter
        val zoomOutPageTransformer = ZoomOutPageTransformer()
        binding.viewPager2.setPageTransformer { page, position ->
            zoomOutPageTransformer.transformPage(page, position)
        }

        binding.dotsIndicator.setViewPager2(binding.viewPager2)

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ExpenceViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ExpenceViewModel::class.java)
    }


    private fun setClickListeners() {
        binding.appbarGreeting.text = "Hello ${AppPreferences.firstName}!"

        binding.appbarEdit.setOnClickListener {
      if(claimDetail!=null) {


          if(claimType=="Expense")
          {
              val intent = Intent(this, MainActivity::class.java)
              intent.putExtra("details_edit", claimDetail)
              intent.putExtra("call_by", "rm_edit")
              intent.putExtra("request_type", "claim")

              startActivity(intent)

          }else{
              val intent = Intent(this, MainActivity::class.java)
              intent.putExtra("details_edit", mileageDetail)
              intent.putExtra("call_by", "rm_edit")
              intent.putExtra("request_type", "mileage")

              startActivity(intent)
          }


        }

        }
        binding.layoutBack.setOnClickListener {
            finish()
        }
        binding.bottomOptionReject.setOnClickListener {
            showRejectAlert(requestId)
        }
        binding.bottomOptionAccept.setOnClickListener {
            acceptRequest(requestId)
        }
    }
    private fun getDetails(claim_id :String) {
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", claim_id.toInt())
        }
        viewModel.getDetails(jsonObject,claim_id).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            claimDetail=response
                            val details=claimDetail.main_claim
                            try {
                                if(details!=null) {

                                     rmStatus=details.reportingMStatus
                                     fmStatus=details.financeMStatus
                                     status=details.status
                                    editSetting(rmStatus,fmStatus,status)


                                    binding.txtGroupName.text = details.expenseGroupName
                                    binding.txtDisc.text = details.description
                                    binding.txtMerchantName.text = details.merchant
                                    binding.txtNetAmount.text = details.currencySymbol + " " + String.format("%.2f", details.netAmount.toString().toDouble())
                                    binding.txtTax.text = details.currencySymbol + " " + String.format("%.2f", details.tax.toString().toDouble())
                                    binding.txtTotalAmount.text = details.currencySymbol + " " + String.format("%.2f", details.totalAmount.toString().toDouble())
                                    binding.txtExpenceType.text =
                                        details.expenseTypeName.toString()
                                    binding.labelDate.text = "Date Of Claim"
                                    binding.txtTripDate.text = Utils.getFormattedDate(
                                        details.date_of_receipt,
                                        Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,
                                        ""
                                    )
                                }
                            } catch (e: Exception) {
                            }

                        }
                    }
                    Status.ERROR -> {
                        Log.e("Reporting Manger", "claim details : ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun getMileageDetails(claim_id :String) {
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", claim_id.toInt())
        }
        viewModel.getMielageDetails(jsonObject,claim_id).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            mileageDetail=response
                            val details=mileageDetail.main_claim
                            try {
                                if(details!=null) {
                                    rmStatus=details.reportingMStatus
                                    fmStatus=details.financeMStatus
                                    status=details.status
                                    editSetting(rmStatus,fmStatus,status)

                                    binding.labelDate.text = "Date Of Trip"
                                    binding.lnMilesKm.visibility = View.VISIBLE
                                    binding.lnClaimMiles.visibility = View.VISIBLE
                                    binding.lnFrom.visibility = View.VISIBLE
                                    binding.lnTo.visibility = View.VISIBLE
                                    binding.lnRound.visibility = View.VISIBLE
                                    binding.lnCarType.visibility = View.VISIBLE

                                    binding.txtGroupName.text = details?.groupName
                                    binding.txtDisc.text = details?.description
                                    binding.txtMerchantName.text = details?.merchant
                                    binding.txtNetAmount.text = details?.currencySymbol + " " + String.format("%.2f", details?.netAmount.toString().toDouble())
                                    binding.txtTax.text = details?.currencySymbol + " " + String.format("%.2f", details?.mtax.toString().toDouble())
                                    binding.txtTotalAmount.text = details?.currencySymbol + " " + String.format("%.2f", details?.totalAmount.toString().toDouble())
                                    binding.txtmilesKm.text = String.format("%.2f", details?.distance.toString().toDouble())
                                    binding.txtClamidMiles.text = String.format("%.2f", details?.claimedMileage.toString().toDouble())
                                    binding.txtExpenceType.text = details?.expense_type_name.toString()
                                    binding.txtTo.text = details?.toLocation
                                    binding.txtFrom.text = details?.fromLocation
                                    binding.txtCarType.text = details?.carType
                                    binding.tvMilesKm.text = details?.type
                                    binding.txtTripDate.text = Utils.getFormattedDate(details?.tripDate.toString(), Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT, "")
                                    if (details?.isRoundTrip == "1") {
                                        binding.txtRoundTrip.text = "Yes"
                                    } else {
                                        binding.txtRoundTrip.text = "No"

                                    }
                                }
                            } catch (e: Exception) {
                            }

                        }
                    }
                    Status.ERROR -> {
                        Log.e("Reporting Manger", "claim details : ${it.message}")
                        it.message?.let { it1 -> Toast.makeText(this, it1, Toast.LENGTH_SHORT).show() }
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun editSetting(rmStatus :String,fmStatus :String,status :String) {
        when (AppPreferences.userType) {
            "Reporting Manager" -> {
                when (rmStatus) {
                    Constants.STATUS_PENDING -> {
                        binding.appbarEdit.visibility=View.VISIBLE
                    }
                    Constants.STATUS_APPROVED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                    Constants.STATUS_REJECTED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                }


            }
            "Finance Department" -> {
                when (fmStatus) {
                    Constants.STATUS_PENDING -> {
                        binding.appbarEdit.visibility=View.VISIBLE
                    }
                    Constants.STATUS_APPROVED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                    Constants.STATUS_REJECTED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                }
            }
            "Final Approver" -> {
                when (status) {
                    Constants.STATUS_PENDING -> {
                        binding.appbarEdit.visibility=View.VISIBLE
                    }
                    Constants.STATUS_APPROVED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                    Constants.STATUS_REJECTED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                }
            }
            "Admin" -> {
                when (status) {
                    Constants.STATUS_PENDING -> {
                        binding.appbarEdit.visibility=View.VISIBLE
                    }
                    Constants.STATUS_APPROVED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                    Constants.STATUS_REJECTED -> {
                        binding.appbarEdit.visibility=View.GONE

                    }
                }
            }
        }
    }

    private fun acceptRejectObserver(accept_reject: Int, reson: String,requestId:String) {
        val data = JsonObject()
        val jsonIdArray = JsonArray()
        jsonIdArray.add(requestId.toInt())
        data.add("claim_ids", jsonIdArray)
        data.addProperty("action", accept_reject)
        data.addProperty("user_id",requestid)
        if (accept_reject == 2) {
            data.addProperty("reason", reson)

        }
        data.addProperty("role", AppPreferences.userType)//////////////////////////
        viewModel.acceptReject(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    Status.ERROR -> {
                        it.message?.let { it1 ->
                            Toast.makeText(this, it1, Toast.LENGTH_SHORT).show()
                        }
                    }
                    Status.LOADING -> {
//                        binding.mProgressBars.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showRejectAlert(requestId:String) {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.reject_alert_dialog)
        val input = dialog.findViewById(R.id.edtDescription) as EditText
        val yesBtn = dialog.findViewById(R.id.btnSubmit) as Button
        val noBtn = dialog.findViewById(R.id.btnCancel) as TextView

        yesBtn.setOnClickListener {

            if (input.text.isNullOrEmpty()) {
                input.error = "Please enter the reason"
            } else {
                input.error = null
                dialog.dismiss()
                println("reason :" + input.text)
                acceptRejectObserver(2, input.text.toString(),requestId)
            }

            //   logoutUser()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun acceptRequest(requestId:String) {
        this?.let { activity ->
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
            title.text = resources.getString(R.string.accept_claim)
            body.text = resources.getString(R.string.are_you_sure_you_want_to_accept_claim)
            yesBtn.text = "Yes"
            noBtn.text = "No"

            yesBtn.setOnClickListener {
                dialog.dismiss()
                if (NoInternetUtils.isConnectedToInternet(activity))
                    acceptRejectObserver(1, "",requestId)

                else
                    Toast.makeText(
                        activity,
                        getString(R.string.check_internet_msg),
                        Toast.LENGTH_SHORT
                    ).show()
            }
            noBtn.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }


    private fun showImagePopup(imageUrl:String) {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_popup_dialog)

        val image = dialog.findViewById(R.id.itemImage) as ImageView
        Glide.with(this)
            .load(imageUrl)
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