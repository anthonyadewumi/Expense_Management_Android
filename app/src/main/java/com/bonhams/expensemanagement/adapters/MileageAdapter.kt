package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.responses.MileageDetail
import kotlinx.android.synthetic.main.item_claims_and_mileage.view.*


class MileageAdapter(
    var listMileage: List<MileageDetail>?
) : RecyclerView.Adapter<MileageAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_claims_and_mileage, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = listMileage?.get(position)
        orderItem?.let { holder.bindItems(it) }

    }

    override fun getItemCount(): Int {
        listMileage?.let {
            return listMileage?.size!!
        } ?: kotlin.run {
            return 0
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: MileageDetail) {
            itemView.tvTitle.text = item.title.replaceFirstChar(Char::uppercase)
//            itemView.tvSubmittedOn.text = item.
            itemView.tvTotalAmount.text = "$" + item.totalAmount
            itemView.tvStatus.text = item.reportMStatus.replaceFirstChar(Char::uppercase)
            when {
                item.reportMStatus.equals("Pending", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorTextDarkGray))
                item.reportMStatus.equals("Approved", true) -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorGreen))
                else -> itemView.tvStatus.setTextColor(itemView.context.resources.getColor(R.color.colorRed))
            }
        }
    }
}