package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.ToBeAcceptedData
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBatchBinding
import com.bonhams.expensemanagement.databinding.ItemExpenceItemRowBinding
import com.bonhams.expensemanagement.ui.rmExpence.ExpenceToBeAccepted
import com.bonhams.expensemanagement.ui.rmExpence.ReportingMangerExpenceDetails
import com.bonhams.expensemanagement.utils.RecylerCallback
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ExpencesListAdapter(
    var expncelist: List<ToBeAcceptedData>,var context:Context,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<ExpencesListAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val attachmentItem = expncelist?.get(position)
       holder.bindItems(expncelist[position],context,expncelist,recylerCallback,position)


    }
    override fun getItemCount(): Int {
        expncelist.let {
            return expncelist.size
        }

    }

    class ViewHolder(itemBinding: ItemExpenceItemRowBatchBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemExpenceItemRowBatchBinding = itemBinding
        fun bindItems(item: ToBeAcceptedData,context:Context,expncelist: List<ToBeAcceptedData>,recylerCallback: RecylerCallback,postion: Int) {


           binding.tvBatchNo.text = item.batch_allotted.toString()
            binding.tvCurrency.text = item.currency_type
           binding.tvCompany.text = item.company_code
            binding.tvLedger.text = item.ledger_id

            binding.tvTotalAmount.text = item.currency_type+" "+String.format("%.2f",item.totalAmount.toString().toDouble())
            binding.tvDate.text =parseDateFormat(item.lastestSubmissionDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM")

            if(item.e_claims>0){
                binding.claimView.visibility= View.VISIBLE
                binding.tvNoClaims.visibility= View.VISIBLE
                binding.tvNoClaims.text = item.e_claims.toString()+" "+"Claims"
            }else{
                binding.claimView.visibility= View.GONE
                binding.tvNoClaims.visibility= View.GONE

            }
            if(item.m_claims>0){
                binding.mileageView.visibility= View.VISIBLE
                binding.tvNoMileage.visibility= View.VISIBLE
                binding.tvNoMileage.text = item.m_claims.toString()+" "+"Mileage"

            }else{
                binding.mileageView.visibility= View.GONE
                binding.tvNoMileage.visibility= View.GONE
            }

/*
            binding.tvName.text = item.employeeName
            binding.tvDeparment.text = item.userType
            binding.tvAmount.text = item.currency_type.toString()+" "+String.format("%.2f",item.totalAmount.toDouble())
            binding.tvtotalClaims.text = item.totalClaims.toString()
            binding.tvemId.text = "Employee ID: "+item.employeeId
            //val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ")
            binding.tvdate.text =parseDateFormat(item.lastestSubmissionDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM yy")*/
            binding.batchCardView.setOnClickListener {
                println("employeeId"+expncelist[adapterPosition].employeeId)
                println("employeeName"+expncelist[adapterPosition].employeeName)
                println("batch_allotted"+expncelist[adapterPosition].batch_allotted)
                val fp = Intent(context, ReportingMangerExpenceDetails::class.java)
                fp.putExtra("employeeId",expncelist[adapterPosition].employeeId.toString())
                fp.putExtra("employeeName",expncelist[adapterPosition].employeeName)
                fp.putExtra("currency_type",expncelist[adapterPosition].currency_type)
                fp.putExtra("batch_allotted",expncelist[adapterPosition].batch_allotted.toString())

                context.startActivity(fp)
            }
            binding.lnMore.setOnClickListener {

                recylerCallback.callback("batch",item,postion)
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
                        R.layout.item_expence_item_row_batch, parent, false
                    )
                )
            }
        }
    }


}