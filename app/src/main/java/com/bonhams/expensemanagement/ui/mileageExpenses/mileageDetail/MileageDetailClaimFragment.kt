package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

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
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.databinding.FragmentMileageClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils

private const val TAG = "ClaimDetailFragment"

class MileageDetailFragment() : Fragment() {

    private var contextActivity: BaseActivity? = null
    private lateinit var mileageDetail: MileageDetail
    private lateinit var binding: FragmentMileageClaimDetailBinding
    private lateinit var attachmentsAdapter: AttachmentsAdapter
    private lateinit var viewModel: MileageDetailClaimViewModel
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mileage_claim_detail, container, false)
        val view = binding.root
        binding.lifecycleOwner = this
        contextActivity = activity as? BaseActivity

        setupViewModel()
        setupAttachmentRecyclerView()
        setupView()

        return view
    }


    fun setMileageDetails(detail: MileageDetail?){
        detail?.let {
            mileageDetail = it
        }
    }

    private fun setupView(){
        if(this::mileageDetail.isInitialized) {
            binding.tvTitle.text = mileageDetail.title
            binding.tvCompanyName.text = mileageDetail.companyName
            binding.tvMileageType.text = mileageDetail.department
            binding.tvDepartment.text = mileageDetail.department
            binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                mileageDetail.submittedOn,
                Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
            )
            binding.tvExpenseType.text = mileageDetail.type
            binding.tvMerchantName.text = mileageDetail.merchant
            binding.tvDateOfTrip.text = Utils.getFormattedDate(
                mileageDetail.tripDate,
                Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT
            )
            binding.tvTripFrom.text = mileageDetail.fromLocation
            binding.tvTripTo.text = mileageDetail.toLocation
            binding.tvDistance.text = mileageDetail.distance
            binding.tvCarType.text = mileageDetail.carType
            binding.tvClaimedMiles.text = mileageDetail.claimedMileage
            binding.tvCurrency.text = mileageDetail.currency
            binding.tvPetrolAmount.text = mileageDetail.petrolAmount
            binding.tvParkAmount.text = mileageDetail.parking
            binding.tvTotalAmount.text = mileageDetail.totalAmount
            binding.tvTax.text = mileageDetail.tax
            binding.tvNetAmount.text = mileageDetail.netAmount
            binding.tvDescription.text = mileageDetail.description

            binding.switchRoundTrip.isChecked = mileageDetail.isRoundTrip == "1"
            binding.switchRoundTrip.isEnabled = false
            /*if(mileageDetail.attachments.trim().isEmpty())
            viewModel.attachmentsList.add(mileageDetail.attachments)*/
        }
        refreshAttachments()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this,
            MileageDetailClaimViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MileageDetailClaimViewModel::class.java)

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
        attachmentsAdapter = AttachmentsAdapter(viewModel.attachmentsList,"detalis")
        binding.rvAttachments.adapter = attachmentsAdapter
    }

    private fun refreshAttachments(){
        if(viewModel.attachmentsList.size > 0){
            binding.tvAttachments.visibility = View.VISIBLE
            binding.rvAttachments.visibility = View.VISIBLE
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            binding.tvAttachments.visibility = View.INVISIBLE
            binding.rvAttachments.visibility = View.GONE
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(contextActivity, view)
        popup.inflate(R.menu.claims_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_copy -> {
                    val fragment = NewMileageClaimFragment()
                    fragment.setMileageDetails(mileageDetail)
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