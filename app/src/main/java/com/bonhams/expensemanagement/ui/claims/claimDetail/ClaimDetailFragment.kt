package com.bonhams.expensemanagement.ui.claims.claimDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils


class ClaimDetailFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var claimDetail: ClaimDetail
    private lateinit var viewModel: ClaimDetailViewModel
    private lateinit var binding: FragmentClaimDetailBinding
    private lateinit var attachmentsAdapter: AttachmentsAdapter

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

        return view
    }

    fun setClaimDetails(detail: ClaimDetail?){
        detail?.let {
            claimDetail = it
        }
    }

    private fun setupView(){
        binding.tvMerchantName.text = claimDetail.merchant
        binding.tvExpenseGroup.text = claimDetail.expenseGroupName
        binding.tvExpenseType.text = claimDetail.expenseTypeName
        binding.tvCompanyNumber.text = claimDetail.companyName
        binding.tvDepartment.text = claimDetail.department
        binding.tvDateOfSubmission.text = Utils.getFormattedDate(claimDetail.createdOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT)
        binding.tvCurrency.text = claimDetail.currencyTypeName
        binding.tvTotalAmount.text = claimDetail.totalAmount
        binding.tvTax.text = claimDetail.tax
        binding.tvNetAmount.text = claimDetail.netAmount
        binding.tvRMStatus.text = claimDetail.reportingMStatus
        binding.tvFMStatus.text = claimDetail.financeMStatus
        binding.tvDescription.text = claimDetail.description

        if(claimDetail.attachments?.trim().isNullOrEmpty())
            viewModel.attachmentsList.add(claimDetail.attachments)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ClaimDetailViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ClaimDetailViewModel::class.java)
    }

    private fun setupAttachmentRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvAttachments.layoutManager = linearLayoutManager
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList)
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun refreshAttachments(){
        if(viewModel.attachmentsList.size > 0){
            binding.tvAttachments.visibility = View.GONE
            binding.rvAttachments.visibility = View.VISIBLE
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            binding.rvAttachments.visibility = View.GONE
            binding.tvAttachments.visibility = View.VISIBLE
        }
    }
}