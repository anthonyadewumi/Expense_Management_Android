package com.bonhams.expensemanagement.adapters

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
import com.bonhams.expensemanagement.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

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
            binding.tvSubmittedOn.text = Utils.getFormattedDate(item.submittedOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,"")
           // binding.tvTotalAmount.text = item.currencySymbol + item.totalAmount
            binding.tvTotalAmount.text = item.currencySymbol + String.format("%.2f",item.totalAmount.toDouble())

            val attachment=item.attachments.split(",")
            if(attachment.isNotEmpty()){
                Glide.with(itemView.context)
                    .load(attachment[0])
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.mountains)
                            .error(R.drawable.mountains)
                    )
                    .placeholder(R.drawable.mountains)
                    .error(R.drawable.mountains)
                    .into(binding.ivClaimImage);
            }

            if(!item.status.isNullOrEmpty()) {
                binding.tvStatus.text =
                    item.status.replaceFirstChar(Char::uppercase)

                when {
                    item.status.equals(Constants.STATUS_PENDING, true) -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                    item.status.equals(Constants.STATUS_APPROVED, true) ->binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                    else -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
                }
            }

            binding.tvCreateCopy.setOnClickListener(View.OnClickListener {
                claimListener.onClaimCreateCopyClicked(item, position)
            })

            binding.claimCardView.setOnClickListener(View.OnClickListener {
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