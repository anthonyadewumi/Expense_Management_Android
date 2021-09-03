package com.bonhams.expensemanagement.ui.claims.claimDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils


class ClaimDetailFragment() : Fragment() {

    private val TAG = javaClass.simpleName
    private var contextActivity: BaseActivity? = null
    private lateinit var claimDetail: ClaimDetail
    private lateinit var viewModel: ClaimDetailViewModel
    private lateinit var binding: FragmentClaimDetailBinding
    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

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
        if(this::claimDetail.isInitialized) {
            binding.tvMerchantName.text = claimDetail.merchant
            binding.tvExpenseGroup.text = claimDetail.expenseGroupName
            binding.tvExpenseType.text = claimDetail.expenseTypeName
            binding.tvCompanyNumber.text = claimDetail.companyName
            binding.tvDepartment.text = claimDetail.department
            binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                claimDetail.createdOn,
                Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
            )
            binding.tvCurrency.text = claimDetail.currencyTypeName
            binding.tvTotalAmount.text = claimDetail.totalAmount
            binding.tvTax.text = claimDetail.tax
            binding.tvNetAmount.text = claimDetail.netAmount
            binding.tvRMStatus.text = claimDetail.reportingMStatus
            binding.tvFMStatus.text = claimDetail.financeMStatus
            binding.tvDescription.text = claimDetail.description

            if (claimDetail.attachments.trim().isEmpty()) {
                viewModel.attachmentsList.add(claimDetail.attachments)
            }
        }
        refreshAttachments()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            ClaimDetailViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(ClaimDetailViewModel::class.java)

        mainViewModel.appbarMenuClick?.observe(viewLifecycleOwner, {
            showPopupMenu(it)
        })
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

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(contextActivity, view)
        popup.inflate(R.menu.claims_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_copy -> {
                    val fragment = NewClaimFragment()
                    fragment.setClaimDetails(claimDetail)
//                    fragment.setRefreshPageListener(this)
                    (contextActivity as? MainActivity)?.addFragment(fragment)
                }
                R.id.action_delete -> {

                }
            }
            true
        })

        popup.show()
    }
}