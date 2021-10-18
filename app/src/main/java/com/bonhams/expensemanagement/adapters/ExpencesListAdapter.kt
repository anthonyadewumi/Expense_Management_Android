package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding
import com.bonhams.expensemanagement.ui.rmExpence.ExpenceToBeAccepted
import com.bonhams.expensemanagement.ui.rmExpence.ReportingMangerExpenceDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ExpencesListAdapter(
    var expncelist: List<ToBeAcceptedData>,var context:Context
) : RecyclerView.Adapter<ExpencesListAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val attachmentItem = expncelist?.get(position)
       holder.bindItems(expncelist[position],context,expncelist)


    }
    override fun getItemCount(): Int {
        expncelist.let {
            return expncelist.size
        }

    }

    class ViewHolder(itemBinding: ItemExpenceItemRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemExpenceItemRowBinding = itemBinding
        fun bindItems(item: ToBeAcceptedData,context:Context,expncelist: List<ToBeAcceptedData>) {
            binding.tvName.text = item.employeeName
            binding.tvDeparment.text = item.userType
            binding.tvamount.text = "$ "+item.totalAmount
            binding.tvtotalClaims.text = item.totalClaims.toString()
            binding.tvemId.text = "Employee ID: "+item.employeeId
            //val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")
            binding.tvdate.text =parseDateFormat(item.lastestSubmissionDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yy")
            binding.claimCardView.setOnClickListener {
                println("employeeId"+expncelist[adapterPosition].employeeId)
                println("employeeName"+expncelist[adapterPosition].employeeName)
                val fp = Intent(context, ReportingMangerExpenceDetails::class.java)
                fp.putExtra("employeeId",expncelist[adapterPosition].employeeId.toString())
                fp.putExtra("employeeName",expncelist[adapterPosition].employeeName)

                context.startActivity(fp)
            }

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
                        R.layout.item_expence_item_row, parent, false
                    )
                )
            }
        }
    }


}