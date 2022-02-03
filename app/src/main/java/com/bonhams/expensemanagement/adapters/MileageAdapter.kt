package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.databinding.ItemClaimsAndMileageBinding
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class MileageAdapter() : PagingDataAdapter<MileageDetail, MileageAdapter.ViewHolder>(MILEAGE_COMPARATOR) {

    private lateinit var mileageListener: OnMileageExpenseClickListener

    fun setupClaimListener(listener: OnMileageExpenseClickListener){
        mileageListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val claimItem = getItem(position)
        claimItem?.let { holder.bind(it, position, mileageListener) }
    }

    class ViewHolder(itemBinding: ItemClaimsAndMileageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemClaimsAndMileageBinding = itemBinding

        fun bind(item: MileageDetail, position: Int, mileageListener: OnMileageExpenseClickListener) {
            binding.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
            binding.tvSubmittedOn.text = Utils.getFormattedDate(item.submittedOn, Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,"")
            binding.tvTotalAmount.text = item.currencySymbol + item.totalAmount
            binding.tvStatus.text = item.status.replaceFirstChar(Char::uppercase)
          //  binding.tvStatus.text = item.reportingMStatus.replaceFirstChar(Char::uppercase)

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
            binding.ivClaimImage.setOnClickListener {
                if(attachment.isNotEmpty()) {
                    mileageListener.onMileageImageClicked(attachment[0], position)
                }

            }
            when {
                item.status.equals(Constants.STATUS_PENDING, true) -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.status.equals(Constants.STATUS_APPROVED, true) ->binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> binding.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
            }


            binding.tvCreateCopy.setOnClickListener(View.OnClickListener {
                mileageListener.onMileageExpenseCreateCopyClicked(item, position)
            })

            binding.claimCardView.setOnClickListener(View.OnClickListener {
                mileageListener.onMileageExpenseItemClicked(item, position)
            })
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.item_claims_and_mileage, parent, false))
            }
        }
    }

    companion object {
        private val MILEAGE_COMPARATOR = object : DiffUtil.ItemCallback<MileageDetail>() {
            override fun areItemsTheSame(oldItem: MileageDetail, newItem: MileageDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MileageDetail, newItem: MileageDetail): Boolean =
                oldItem == newItem
        }
    }

    interface OnMileageExpenseClickListener{
        fun onMileageExpenseItemClicked(claim: MileageDetail?, position: Int)
        fun onMileageImageClicked(imageUrl: String?, position: Int)
        fun onMileageExpenseCreateCopyClicked(claim: MileageDetail?, position: Int)
    }
}