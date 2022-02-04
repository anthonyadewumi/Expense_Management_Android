package com.bonhams.expensemanagement.ui.mileageExpenses.mileageDetail

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.adapters.AttachmentsAdapter
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.data.services.ApiHelper
import com.bonhams.expensemanagement.data.services.RetrofitBuilder
import com.bonhams.expensemanagement.data.services.responses.CommonResponse
import com.bonhams.expensemanagement.databinding.FragmentMileageClaimDetailBinding
import com.bonhams.expensemanagement.ui.BaseActivity
import com.bonhams.expensemanagement.ui.main.MainActivity
import com.bonhams.expensemanagement.ui.main.MainViewModel
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.EditMileageClaimFragment
import com.bonhams.expensemanagement.ui.mileageExpenses.newMileageClaim.NewMileageClaimFragment
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.Status
import com.bonhams.expensemanagement.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import org.imaginativeworld.oopsnointernet.utils.NoInternetUtils

private const val TAG = "ClaimDetailFragment"

class MileageDetailFragment() : Fragment() , RecylerCallback {

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
        setClickListner()
        return view
    }

    override fun onResume() {
        super.onResume()
        setupAttachmentRecyclerView()

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
            binding.tvMileageType.text = mileageDetail.type
            binding.tvDepartment.text = mileageDetail.cost_code
            binding.tvDateOfSubmission.text = Utils.getFormattedDate(
                mileageDetail.submittedOn,
                Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
            )
            binding.tvExpenceGroup.text = mileageDetail.groupName
            binding.tvExpenceType.text = mileageDetail.expense_type_name
           // binding.tvExpenseType.text = mileageDetail.type
            binding.tvMerchantName.text = mileageDetail.merchant
            binding.tvDateOfTrip.text = Utils.getFormattedDate(
                mileageDetail.tripDate,
                Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
            )
            binding.tvTripFrom.text = mileageDetail.fromLocation
            binding.tvTripTo.text = mileageDetail.toLocation
            binding.tvDistance.text = mileageDetail.distance
            binding.tvCarType.text = mileageDetail.carType
            binding.tvClaimedMiles.text = mileageDetail.claimedMileage
            binding.tvCurrency.text = mileageDetail.currencyTypeNamename
            binding.tvPetrolAmount.text =mileageDetail.currencySymbol+" "+String.format("%.2f",mileageDetail.petrolAmount.toDouble())
            binding.tvParkAmount.text = mileageDetail.currencySymbol+" "+String.format("%.2f",mileageDetail.parking.toDouble())
            binding.tvTotalAmount.text = mileageDetail.currencySymbol+" "+String.format("%.2f",mileageDetail.totalAmount.toDouble())
            binding.tvApplicableTax.text = mileageDetail.currencySymbol+" "+String.format("%.2f",mileageDetail.mtax.toDouble())
            binding.tvNetAmount.text = mileageDetail.currencySymbol+" "+String.format("%.2f",mileageDetail.netAmount.toDouble())
            binding.tvTaxCode.text = mileageDetail.tax_code
            binding.tvDescription.text = mileageDetail.description
            binding.tvRMStatus.text = mileageDetail.reportingMStatus
            binding.tvFMStatus.text = mileageDetail.financeMStatus
            binding.tvMileageRate.text = mileageDetail.mileageRate

            if (!mileageDetail.attachments.isNullOrEmpty() && mileageDetail.attachments.trim().isNotEmpty()) {

                val attachment=mileageDetail.attachments.split(",")
                attachment.forEach {
                    viewModel.attachmentsList.add(it)
                }

            }

            if(mileageDetail.type=="KM") {
                binding.txtMilesKM.text = "Claimed KM"
                binding.tvCalculatedDistance.text = "Calculated KM"
            }else{
                binding.txtMilesKM.text="Claimed Miles"
                binding.tvCalculatedDistance.text="Calculated Miles"

            }

            when (mileageDetail.reportingMStatus) {
                Constants.STATUS_PENDING -> {
                    binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextDarkGray))

                    binding.ivRMReminder.visibility=View.VISIBLE

                }
                Constants.STATUS_APPROVED -> {
                    binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorGreen))
                    binding.tvRMStatusDate.visibility=View.VISIBLE
                    try {
                        binding.tvRMStatusDate.text = Utils.getFormattedDate(
                            mileageDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )
                    } catch (e: Exception) {
                    }

                }
                else -> {
                    binding.tvRMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRed))
                    binding.tvRMStatusDate.visibility=View.VISIBLE
                    try {
                        binding.tvRMStatusDate.text = Utils.getFormattedDate(
                            mileageDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )
                    } catch (e: Exception) {
                    }

                }
            }
            when (mileageDetail.financeMStatus) {
                Constants.STATUS_PENDING -> {
                    binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorTextDarkGray))
                    binding.ivFMReminder.visibility=View.VISIBLE


                }
                Constants.STATUS_APPROVED -> {
                    binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorGreen))
                    binding.tvFMStatusDate.visibility=View.VISIBLE
                    try {
                        binding.tvFMStatusDate.text = Utils.getFormattedDate(
                            mileageDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )
                    } catch (e: Exception) {
                    }

                }
                else -> {
                    binding.tvFMStatus.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorRed))
                    binding.tvFMStatusDate.visibility=View.VISIBLE
                    try {
                        binding.tvFMStatusDate.text = Utils.getFormattedDate(
                            mileageDetail.rm_updation_date,
                            Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,""
                        )
                    } catch (e: Exception) {
                    }

                }
            }
            binding.switchRoundTrip.isChecked = mileageDetail.isRoundTrip == "1"
            binding.switchRoundTrip.isEnabled = false
            /*if(mileageDetail.attachments.trim().isEmpty())
            viewModel.attachmentsList.add(mileageDetail.attachments)*/
        }
        refreshAttachments()
    }
    private fun setClickListner() {
        binding.ivRMReminder.setOnClickListener {
            showReminderAlert("Are you sure you want to send reminder to the Reporting  Manager?","RM")

        }
        binding.ivFMReminder.setOnClickListener {
            showReminderAlert("Are you sure you want to send reminder to the Finance Manager?","FD")

           // showReminderAlert("We have sent a reminder to Finance Manager for approval.","FD")

        }
        binding.rlDetails.setOnClickListener {
            if(binding.lnDetailsView.isVisible){
                binding.lnDetailsView.visibility=View.GONE
                binding.ivDetailsDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnDetailsView.visibility=View.VISIBLE
                binding.ivDetailsDropDown.setImageResource(R.drawable.drop_down)
            }
        }
        binding.rlMilegae.setOnClickListener {
            if(binding.lnMeilageContant.isVisible){
                binding.lnMeilageContant.visibility=View.GONE
                binding.ivMeliageDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnMeilageContant.visibility=View.VISIBLE
                binding.ivMeliageDropDown.setImageResource(R.drawable.drop_down)
            }
        }
        binding.rlDefault.setOnClickListener {
            if(binding.lnDefaultContent.isVisible){
                binding.lnDefaultContent.visibility=View.GONE
                binding.ivDefaultDropDown.setImageResource(R.drawable.drop_right)

            }else{
                binding.lnDefaultContent.visibility=View.VISIBLE
                binding.ivDefaultDropDown.setImageResource(R.drawable.drop_down)
            }
        }
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
            binding.tvAttachments.visibility = View.INVISIBLE
            binding.rvAttachments.visibility = View.GONE
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(contextActivity, view)
        popup.inflate(R.menu.claims_menu)
        if(mileageDetail.reportingMStatus==Constants.STATUS_PENDING||mileageDetail.reportingMStatus==Constants.STATUS_REJECTED&&mileageDetail.financeMStatus==Constants.STATUS_PENDING||mileageDetail.financeMStatus==Constants.STATUS_REJECTED){

        }else{
            popup.menu.findItem(R.id.action_edit).isVisible = false

        }
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_edit -> {
                    val fragment = EditMileageClaimFragment()
                    fragment.setMileageDetails(mileageDetail)
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
            //noBtn.text = resources.getString(R.string.cancel)

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
    private fun sendReminder(userType:String){
        val jsonObject = JsonObject().also {
            it.addProperty("claim_id", mileageDetail.id)
            it.addProperty("for_user", userType)
        }
        viewModel.sendReminder(jsonObject).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "sendReminder: ${resource.status}")
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
    private fun setResponseForReminder(commonResponse: CommonResponse){
        Toast.makeText(contextActivity, commonResponse.message, Toast.LENGTH_SHORT).show()

    }
    private fun deleteClaim(){
        viewModel.deleteClaim(viewModel.getDeleteClaimRequest(mileageDetail.id)).observe(viewLifecycleOwner, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.let { response ->
                            try {
                                Log.d(TAG, "deleteClaim: ${resource.status}")
                                mainViewModel.isMileageListRefresh?.value=true
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