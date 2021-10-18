package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.ExpenceDetailsData
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.databinding.ItemExpenceDetailsRowBinding
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding
import com.bonhams.expensemanagement.ui.rmExpence.ExpenceToBeAccepted
import com.bonhams.expensemanagement.ui.rmExpence.ReportingMangerExpenceDetails
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ExpencesDetailsListAdapter(
    var expncelist: List<ExpenceDetailsData>,var context:Context
) : RecyclerView.Adapter<ExpencesDetailsListAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val attachmentItem = expncelist?.get(position)
       holder.bindItems(expncelist[position],context)


    }
    override fun getItemCount(): Int {
        expncelist.let {
            return expncelist.size
        }

    }

    class ViewHolder(itemBinding: ItemExpenceDetailsRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemExpenceDetailsRowBinding = itemBinding
        fun bindItems(item: ExpenceDetailsData,context:Context) {
            binding.tvGroupName.text = item.expenseGroupName
            binding.tvExpenseType.text = item.claimType
           binding.tvNetAmount.text = "$ "+item.netAmount
           binding.tvTotalAmount.text = "$ "+item.totalAmount
           binding.tvMerchant.text =item.claimTitle
           // binding.tvtotalClaims.text = item.totalClaims.toString()
           // binding.tvemId.text = "Employee ID: "+item.employeeId
            //val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")
           // binding.tvdate.text =parseDateFormat(item.lastestSubmissionDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yy")
            binding.claimCardView.setOnClickListener {
                //val fp = Intent(context, ReportingMangerExpenceDetails::class.java)
                //context.startActivity(fp)
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
                        R.layout.item_expence_details_row, parent, false
                    )
                )
            }
        }
    }


}