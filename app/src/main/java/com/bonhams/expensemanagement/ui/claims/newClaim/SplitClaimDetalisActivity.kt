package com.bonhams.expensemanagement.ui.claims.newClaim

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.CustomSpinnerAdapter
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.requests.LoginRequest
import com.bonhams.expensemanagement.data.services.requests.NewMileageClaimRequest
import com.bonhams.expensemanagement.data.services.responses.DropdownResponse
import com.bonhams.expensemanagement.data.services.responses.LoginResponse
import com.bonhams.expensemanagement.databinding.ActivityLoginBinding
import com.bonhams.expensemanagement.databinding.ActivitySplitClaimBinding
import com.bonhams.expensemanagement.databinding.ActivitySplitClaimDetailsBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.splitItmlist
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.totalAmount
import com.bonhams.expensemanagement.ui.forgotPassword.ForgotPasswordActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.resetPassword.ResetPasswordActivity
import com.bonhams.expensemanagement.utils.AppPreferences
import com.bonhams.expensemanagement.utils.Status
import com.google.android.material.snackbar.Snackbar
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.snackbars.fire.NoInternetSnackbarFire
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

class SplitClaimDetalisActivity : BaseActivity() {
    private lateinit var binding: ActivitySplitClaimDetailsBinding
    private lateinit var viewModel: NewClaimViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_split_claim_details)
        binding.lifecycleOwner = this
        setupViewModel()
        setClickListeners()
        setupView()
    }

    private fun setupView(){
        val item = intent.getSerializableExtra("SplitItem") as SplitClaimItem?
        val currancyCode = intent.getSerializableExtra("currencyCode") as String?
        val currencySymbol = intent.getSerializableExtra("currencySymbol") as String?
        binding.editCompnyName.setText(item?.compnyName)
        binding.editDepartName.setText(item?.departmentName)
        binding.editExpenceType.setText(item?.expenceTypeName)
        binding.edtTotalAmount.setText(currencySymbol+" "+item?.totalAmount)
        binding.edtTaxCode.setText(item?.taxcode)
        binding.edtTax.setText(currencySymbol+" "+item?.tax.toString())
        binding.edtAutionSales.setText(item?.auctionSales)
        binding.editExpenceCode.setText(item?.expenceCode)
    }
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            NewClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(NewClaimViewModel::class.java)
    }
    private fun setClickListeners(){
        binding.layoutBack.setOnClickListener { finish() }
        binding.lnCancel.setOnClickListener { finish() }

    }





}