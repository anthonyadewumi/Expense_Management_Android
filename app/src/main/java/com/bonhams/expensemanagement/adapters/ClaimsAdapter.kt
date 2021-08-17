package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.services.responses.MileageDetail


class ClaimsAdapter(
    var listClaims: List<MileageDetail>?
) : RecyclerView.Adapter<ClaimsAdapter.ViewHolder>() {

    init {
    }

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
        fun bindItems(item: MileageDetail) {

        }
    }
}