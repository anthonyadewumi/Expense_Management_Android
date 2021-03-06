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
import com.bonhams.expensemanagement.utils.Constants
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bonhams.expensemanagement.utils.RefreshPageListener
import com.bonhams.expensemanagement.utils.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ExpencesDetailsListAdapter(
    var expncelist: List<ExpenceDetailsData>,var context:Context,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<ExpencesDetailsListAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val attachmentItem = expncelist?.get(position)
       holder.bindItems(expncelist[position],context,recylerCallback,position)


    }
    override fun getItemCount(): Int {
        expncelist.let {
            return expncelist.size
        }

    }

    class ViewHolder(itemBinding: ItemExpenceDetailsRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemExpenceDetailsRowBinding = itemBinding
        fun bindItems(item: ExpenceDetailsData,context:Context,recylerCallback: RecylerCallback,postion:Int) {
            binding.tvClaimId.text = item.requestId.toString()
           binding.tvClaimAmount.text = item.currency_type+" "+String.format("%.2f",item.totalAmount)
           binding.tvMerchant.text =item.claimTitle
            binding.tvMerchant.text= Utils.getFormattedDate( item.submittedOn.toString(), Constants.YYYY_MM_DD_SERVER_RESPONSE_FORMAT,"")


            val attachment= item.attachments.split(",")
            Glide.with(context)
                .load(attachment[0])
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.mountains)
                        .error(R.drawable.mountains)
                )
                .placeholder(R.drawable.mountains)
                .error(R.drawable.mountains)
                .into(binding.ivClaimImage)

            binding.chkRequest.setOnClickListener {
                if( binding.chkRequest.isChecked){
                    with(recylerCallback) {
                        callback("checked", item, postion)
                    }
                }else recylerCallback.callback("unchecked",item,postion)
            }
            binding.claimCardView.setOnClickListener {
                with(recylerCallback) {
                    callback("details", item, postion)

                }
            }
        }

        fun parseDateFormat(
            dateToFormat: String?,
            inputFormat: String?,
            outputFormat: String?
        ): String? {
            val inputFormat = SimpleDateFormat(inputFormat)
            val outputFormat = SimpleDateFormat(outputFormat)
            var date: Date?
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