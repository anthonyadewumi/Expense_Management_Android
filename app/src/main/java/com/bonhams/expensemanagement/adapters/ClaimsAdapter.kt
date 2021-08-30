package com.bonhams.expensemanagement.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.ClaimDetail
import com.bonhams.expensemanagement.databinding.ItemClaimsAndMileageBinding
import com.bonhams.expensemanagement.utils.Constants
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*

class ClaimsAdapter() : PagingDataAdapter<ClaimDetail, ClaimsAdapter.ViewHolder>(CLAIM_COMPARATOR) {

    private lateinit var claimListener: OnClaimClickListener

    fun setupClaimListener(listener: OnClaimClickListener){
        claimListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val claimItem = getItem(position)
        claimItem?.let { holder.bind(it, position, claimListener) }
    }

    class ViewHolder(itemBinding: ItemClaimsAndMileageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemClaimsAndMileageBinding = itemBinding

        fun bind(item: ClaimDetail, position: Int, claimListener: OnClaimClickListener) {
            binding.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
            binding.tvSubmittedOn.text = "02 May"
            binding.tvTotalAmount.text = "$" + item.totalAmount
            if(!item.reportingMStatus.isNullOrEmpty()) {
                binding.tvStatus.text =
                    item.reportingMStatus.replaceFirstChar(Char::uppercase)

                when {
                    item.reportingMStatus.equals(Constants.STATUS_PENDING, true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                    item.reportingMStatus.equals(Constants.STATUS_APPROVED, true) ->itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                    else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
                }
            }


            binding.tvCreateCopy.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: 1111111")
                claimListener.onClaimCreateCopyClicked(item, position)
            })

            binding.claimCardView.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: 2222222")
                claimListener.onClaimItemClicked(item, position)
            })
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_claims_and_mileage, parent, false))
            }
        }
    }

    companion object {
        private val CLAIM_COMPARATOR = object : DiffUtil.ItemCallback<ClaimDetail>() {
            override fun areItemsTheSame(oldItem: ClaimDetail, newItem: ClaimDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ClaimDetail, newItem: ClaimDetail): Boolean =
                oldItem == newItem
        }
    }

    interface OnClaimClickListener{
        fun onClaimItemClicked(claim: ClaimDetail?, position: Int)
        fun onClaimCreateCopyClicked(claim: ClaimDetail?, position: Int)
    }
}