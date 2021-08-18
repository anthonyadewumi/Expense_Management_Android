package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.responses.ClaimDetail
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*


class ClaimsAdapter(
    var listClaims: List<ClaimDetail>?
) : RecyclerView.Adapter<ClaimsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_claims_and_mileage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = listClaims?.get(position)
        orderItem?.let { holder.bindItems(it) }

    }

    override fun getItemCount(): Int {
        listClaims?.let {
            return listClaims?.size!!
        } ?: kotlin.run {
            return 0
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item: ClaimDetail) {
            itemView.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
//            itemView.tvSubmittedOn.text = item.
            itemView.tvTotalAmount.text = "$" + item.totalAmount
            itemView.tvStatus.text = item.reportingMStatus.replaceFirstChar(Char::uppercase)
            when {
                item.reportingMStatus.equals("Pending", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.reportingMStatus.equals("Approved", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
            }
        }
    }
}