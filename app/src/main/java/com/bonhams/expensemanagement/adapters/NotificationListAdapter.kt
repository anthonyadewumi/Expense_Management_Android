package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.NotificationData
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding
import com.bonhams.expensemanagement.databinding.ItemNotificationBinding
import com.bonhams.expensemanagement.ui.rmExpence.ExpenceToBeAccepted
import com.bonhams.expensemanagement.ui.rmExpence.ReportingMangerExpenceDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NotificationListAdapter(
    var listOrders: List<NotificationData>, var context:Context
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val attachmentItem = expncelist?.get(position)
       holder.bindItems(listOrders[position],context,listOrders)


    }
    override fun getItemCount(): Int {
        listOrders.let {
            return listOrders?.size
        }

    }

    class ViewHolder(itemBinding: ItemNotificationBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemNotificationBinding = itemBinding
        fun bindItems(item: NotificationData,context:Context,expncelist: List<NotificationData>) {
            binding.tvTitle.text = item.title
            binding.tvMsg.text = item.description

            binding.tvTime.text =parseDateFormat(item.date_time, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yy")
            /*binding.claimCardView.setOnClickListener {

            }*/

        }

        fun parseDateFormat(
            dateToFormat: String?,
            inputFormat: String?,
            outputFormat: String?
        ): String? {
            val inputFormat = SimpleDateFormat(inputFormat)
            val outputFormat = SimpleDateFormat(outputFormat)
            var date: Date? = null
            var str: String? = null
            try {
                date = inputFormat.parse(dateToFormat)
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }


        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_notification, parent, false
                    )
                )
            }
        }
    }


}