package com.bonhams.expensemanagement.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.responses.ClaimDetail
import com.bonhams.expensemanagement.databinding.ItemClaimsAndMileageBinding
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*

class ClaimsAdapter(
    var listClaims: List<ClaimDetail>?,
    var claimListener: OnClaimClickListener
) : RecyclerView.Adapter<ClaimsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_claims_and_mileage, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = listClaims?.get(position)
        orderItem?.let { holder.bindItems(it, position, claimListener) }
    }

    fun setResponse(list: List<ClaimDetail>?) {
        listClaims = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listClaims!!.size
    }

    class ViewHolder(itemBinding: ItemClaimsAndMileageBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        val binding: ItemClaimsAndMileageBinding = itemBinding

        fun bindItems(item: ClaimDetail, position: Int, claimListener: OnClaimClickListener) {
            binding.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
            binding.tvSubmittedOn.text = "02 May"
            binding.tvTotalAmount.text = "$" + item.totalAmount
            binding.tvStatus.text = item.reportingMStatus.replaceFirstChar(Char::uppercase)

            when {
                item.reportingMStatus.equals("Pending", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.reportingMStatus.equals("Approved", true) ->itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
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
    }

    interface OnClaimClickListener{
        fun onClaimItemClicked(claim: ClaimDetail?, position: Int)
        fun onClaimCreateCopyClicked(claim: ClaimDetail?, position: Int)
    }
}