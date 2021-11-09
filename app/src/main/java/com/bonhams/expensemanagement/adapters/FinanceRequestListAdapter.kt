package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.databinding.ItemClaimsAndMileageBinding
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding
import com.bonhams.expensemanagement.ui.rmExpence.ReportingMangerExpenceDetails
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FinanceRequestListAdapter(var context:Context) : PagingDataAdapter<ToBeAcceptedData, FinanceRequestListAdapter.ViewHolder>(CLAIM_COMPARATOR) {

    private lateinit var claimListener: OnClaimClickListener

    fun setupClaimListener(listener: OnClaimClickListener){
        claimListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val claimItem = getItem(position)
        claimItem?.let { holder.bind(it, position, claimListener,context) }
    }

    class ViewHolder(itemBinding: ItemExpenceItemRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemExpenceItemRowBinding = itemBinding

        fun bind(item: ToBeAcceptedData, position: Int, claimListener: OnClaimClickListener,context: Context) {
            binding.tvName.text = item.employeeName.replaceFirstChar(Char::uppercase)
            binding.tvDeparment.text = item.userType.replaceFirstChar(Char::uppercase)
            binding.tvAmount.text = "$ "+item.totalAmount
            binding.tvtotalClaims.text = item.totalClaims.toString()
            binding.tvdate.text =parseDateFormat(item.lastestSubmissionDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yy")
            binding.tvemId.text = "Employee ID: "+item.employeeId
            binding.claimCardView.setOnClickListener {
                println("employeeId"+item.employeeId)
                println("employeeName"+item.employeeName)
                val fp = Intent(context, ReportingMangerExpenceDetails::class.java)
                fp.putExtra("employeeId",item.employeeId.toString())
                fp.putExtra("employeeName",item.employeeName)

                context.startActivity(fp)
            }
            /* binding.tvSubmittedOn.text = Utils.getFormattedDate(item.createdOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT)
             binding.tvTotalAmount.text = item.currencySymbol + item.totalAmount
             if(!item.reportingMStatus.isNullOrEmpty()) {
                 binding.tvStatus.text =
                     item.reportingMStatus.replaceFirstChar(Char::uppercase)

                 when {
                     item.reportingMStatus.equals(Constants.STATUS_PENDING, true) -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                     item.reportingMStatus.equals(Constants.STATUS_APPROVED, true) ->binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                     else -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
                 }
             }*/

          /*  binding.tvCreateCopy.setOnClickListener(View.OnClickListener {
                claimListener.onClaimCreateCopyClicked(item, position)
            })

            binding.claimCardView.setOnClickListener(View.OnClickListener {
                claimListener.onClaimItemClicked(item, position)
            })*/
        }
        fun parseDateFormat(
            dateToFormat: String?,
            inputFormat: String?,
            outputFormat: String?
        ): String? {
            val inputFormat = SimpleDateFormat(inputFormat)
            val outputFormat = SimpleDateFormat(outputFormat)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(dateToFormat)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }
        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_expence_item_row, parent, false))
            }
        }
    }

    companion object {
        private val CLAIM_COMPARATOR = object : DiffUtil.ItemCallback<ToBeAcceptedData>() {
            override fun areItemsTheSame(oldItem: ToBeAcceptedData, newItem: ToBeAcceptedData): Boolean =
                oldItem.employeeId == newItem.employeeId

            override fun areContentsTheSame(oldItem: ToBeAcceptedData, newItem: ToBeAcceptedData): Boolean =
                oldItem == newItem
        }
    }

    interface OnClaimClickListener{
        fun onClaimItemClicked(claim: ClaimDetail?, position: Int)
        fun onClaimCreateCopyClicked(claim: ClaimDetail?, position: Int)
    }
}