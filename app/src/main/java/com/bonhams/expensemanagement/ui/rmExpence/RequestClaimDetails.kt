package com.bonhams.expensemanagement.ui.rmExpence

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.databinding.ActivityExpenceAcceptedBinding
import com.bonhams.expensemanagement.databinding.ActivityReportingMangerExpenceDetailsBinding
import com.bonhams.expensemanagement.databinding.ActivityRequestClaimDetailsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class RequestClaimDetails : BaseActivity(), RecylerCallback {
    private lateinit var binding: ActivityRequestClaimDetailsBinding
    private lateinit var viewModel: ExpenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_request_claim_details)
        binding.lifecycleOwner = this
        setupViewModel()
        setClickListeners()
        setupView()
    }

    private fun setupView() {
        val adapter = ImagePager2Adapter(this, this)
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
        binding.bottomOptionReject.setOnClickListener {
            showRejectAlert()
        }
        binding.bottomOptionAccept.setOnClickListener {
            acceptRequest()
        }
    }


    private fun acceptRejectObserver(accept_reject: Int, reson: String) {
        val data = JsonObject()
        val jsonIdArray = JsonArray()
        data.add("claim_ids", jsonIdArray)
        data.addProperty("action", accept_reject)
        //   data.addProperty("user_id",requestid.toInt())
        if (accept_reject == 2) {
            data.addProperty("reason", reson)

        }

        data.addProperty("role", AppPreferences.userType)
        viewModel.acceptReject(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
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

    private fun showRejectAlert() {
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
                acceptRejectObserver(2, input.text.toString())
            }

            //   logoutUser()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun acceptRequest() {
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
                // deleteClaim()
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


    private fun showImagePopup() {
        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.image_popup_dialog)
        dialog.show()
        val noBtn = dialog.findViewById(R.id.lnClose) as LinearLayout
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun callback(action: String, data: Any, postion: Int) {
        if (action == "show") {
            showImagePopup()
        }
    }
}