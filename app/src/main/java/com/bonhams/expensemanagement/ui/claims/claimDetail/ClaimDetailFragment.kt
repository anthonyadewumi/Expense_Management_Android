package com.bonhams.expensemanagement.ui.claims.claimDetail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.FragmentClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.claims.newClaim.NewClaimFragment
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Status
import com.bonhams.expensemanagement.utils.Utils
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils


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
        try {
            if (this::claimDetail.isInitialized) {
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

                if (!claimDetail.attachments.isNullOrEmpty() && claimDetail.attachments.trim()
                        .isNotEmpty()
                ) {
                    viewModel.attachmentsList.add(claimDetail.attachments)
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
            binding.tvAttachments.visibility = View.VISIBLE
            binding.rvAttachments.visibility = View.VISIBLE
            attachmentsAdapter.notifyDataSetChanged()
        }
        else{
            binding.rvAttachments.visibility = View.GONE
            binding.tvAttachments.visibility = View.INVISIBLE
        }
    }

    private fun deleteClaim(){
        viewModel.deleteClaim(viewModel.getDeleteClaimRequest(claimDetail)).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "deleteClaim: ${resource.status}")
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

    private fun setResponse(commonResponse: CommonResponse){
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()
        if(commonResponse.success){
            (contextActivity as? MainActivity)?.clearFragmentBackstack()
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
}