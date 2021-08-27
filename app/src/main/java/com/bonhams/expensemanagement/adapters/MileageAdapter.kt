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
import com.bonhams.expensemanagement.data.model.MileageDetail
import com.bonhams.expensemanagement.databinding.ItemClaimsAndMileageBinding
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*


class MileageAdapter() : PagingDataAdapter<MileageDetail, MileageAdapter.ViewHolder>(MILEAGE_COMPARATOR) {

    private lateinit var mileageListener: OnMileageExpenseClickListener

    fun setupClaimListener(listener: OnMileageExpenseClickListener){
        mileageListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MileageAdapter.ViewHolder {
        return MileageAdapter.ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: MileageAdapter.ViewHolder, position: Int) {
        val claimItem = getItem(position)
        claimItem?.let { holder.bind(it, position, mileageListener) }
    }

    class ViewHolder(itemBinding: ItemClaimsAndMileageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        private val binding: ItemClaimsAndMileageBinding = itemBinding

        fun bind(item: MileageDetail, position: Int, mileageListener: OnMileageExpenseClickListener) {
            binding.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
            binding.tvSubmittedOn.text = "02 May"
            binding.tvTotalAmount.text = "$" + item.totalAmount
            binding.tvStatus.text = item.reportMStatus.replaceFirstChar(Char::uppercase)

            when {
                item.reportMStatus.equals("Pending", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.reportMStatus.equals("Approved", true) ->itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
            }

            binding.tvCreateCopy.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: 1111111")
                mileageListener.onMileageExpenseCreateCopyClicked(item, position)
            })

            binding.claimCardView.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: 2222222")
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
        fun onMileageExpenseCreateCopyClicked(claim: MileageDetail?, position: Int)
    }
}