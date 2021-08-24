package com.bonhams.expensemanagement.adapters


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.responses.ClaimDetail
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*


class ClaimsAdapter(
    var listClaims: List<ClaimDetail>?,
    var claimListener: OnClaimClickListener
) : RecyclerView.Adapter<ClaimsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_claims_and_mileage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = listClaims?.get(position)
        orderItem?.let {
            holder.bindItems(it, position, claimListener)
            holder.itemView.cardView.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: $position")
                claimListener.onClaimItemClicked(orderItem, position)
            })
        }
    }

    override fun getItemCount(): Int {
        listClaims?.let {
            return listClaims?.size!!
        } ?: kotlin.run {
            return 0
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: ClaimDetail, position: Int, claimListener: OnClaimClickListener) {
            itemView.tvTitle.text = "TESTINGGGGGGGGG"//item.title.replaceFirstChar(Char::uppercase)
            // itemView.tvSubmittedOn.text = TODO
            itemView.tvTotalAmount.text = "$" + item.totalAmount
            itemView.tvStatus.text = item.reportingMStatus.replaceFirstChar(Char::uppercase)
            when {
                item.reportingMStatus.equals("Pending", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.reportingMStatus.equals("Approved", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
            }

            /*itemView.cardView.setOnClickListener(View.OnClickListener {
                Log.d("ClaimsAdapter", "bindItems: $position")
                claimListener.onClaimItemClicked(item, position)
            })*/

            itemView.tvCreateCopy.setOnClickListener(View.OnClickListener {
                claimListener.onClaimCreateCopyClicked(item, position)
            })
        }
    }

    interface OnClaimClickListener{
        fun onClaimItemClicked(claim: ClaimDetail, position: Int)
        fun onClaimCreateCopyClicked(claim: ClaimDetail, position: Int)
    }
}