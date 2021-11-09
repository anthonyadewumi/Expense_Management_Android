package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.NotificationData
import com.bonhams.expensemanagement.data.model.NotificationItem
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding


class NotificationAdapter(
    var listOrders: List<NotificationData>?
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = listOrders?.get(position)
        orderItem?.let { holder.bindItems(it) }
    }

    override fun getItemCount(): Int {
        return listOrders!!.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item: NotificationData) {
        }
    }
}