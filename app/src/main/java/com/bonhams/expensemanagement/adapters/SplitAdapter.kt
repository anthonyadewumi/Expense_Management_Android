package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.SplitClaimItem
import com.bonhams.expensemanagement.databinding.ItemSplitRowBinding
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimDetalisActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.EditSplitClaimActivity
import com.bonhams.expensemanagement.utils.RecylerCallback


class SplitAdapter(
   var currencyCode :String ,var currencySymbol: String ,var splitList: MutableList<SplitClaimItem?>,var mcontext:Context,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<SplitAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = splitList.get(position)
        attachmentItem?.let { holder.bindItems(position,it,currencyCode,currencySymbol,recylerCallback) }

            holder.removeItems(splitList, position,recylerCallback)
            holder.details(splitList, position,mcontext,currencyCode,currencySymbol,recylerCallback)

    }
    override fun getItemCount(): Int {
        splitList?.let {
            return splitList?.size!!
        }
    }

    class ViewHolder(itemBinding: ItemSplitRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemSplitRowBinding = itemBinding
        fun bindItems(postion:Int,item: SplitClaimItem, currencyCode :String , currencySymbol: String ,recylerCallback: RecylerCallback) {
            binding.tvTitles.text = item.companyCode
           // binding.tvamount.setCurrencySymbol(currencySymbol)
           // binding.tvamount.setLocale(currencyCode)

            if(postion==0){
                binding.ivDeleteSplit.visibility=View.INVISIBLE
            }else{
                binding.ivDeleteSplit.visibility=View.VISIBLE

            }
            try {
                binding.tvCurrencySymbol.text = currencySymbol
                binding.tvamount.setText(String.format("%.2f", item.totalAmount.toDouble()))
              //  binding.tvamount.setText(currencySymbol+" "+item.totalAmount)
            } catch (e: Exception) {
                binding.tvamount.setText(item.totalAmount)

            }

            binding.tvTitle.text = item.expenceTypeName

            //  binding.tvamount.text = currencySymbol+item?.totalAmount
            binding.tvtax.text = "Tax : "+currencySymbol+item?.tax
            binding.tvtaxcode.text = "Tax Code : "+item?.taxcode

            binding.tvamount.doAfterTextChanged {
                var textamount =binding.tvamount.text.toString().toDouble()
                println("chk total amount in details :"+textamount)

                if (textamount != null) {
                        val amount = textamount.toString().toDouble()
                        recylerCallback.callback("update", amount, postion)

                }

            }



        }





        fun removeItems(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,recylerCallback: RecylerCallback) {
                binding.ivDeleteSplit.setOnClickListener {
                    val itemData=listAttachments?.get(postion)

                    val amount= listAttachments?.get(postion)?.totalAmount.toString().toDouble()
                    val tax= listAttachments?.get(postion)?.tax.toString().toDouble()
                    listAttachments?.removeAt(postion)
                    bindingAdapter?.notifyDataSetChanged()
                    if (itemData != null) {
                        recylerCallback.callback("remove",itemData,postion)
                    }
                }

            }
        fun details(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,context:Context,currencyCode :String , currencySymbol: String,recylerCallback: RecylerCallback ) {
            val itemData=listAttachments?.get(postion)
                binding.tvTitle.setOnClickListener {
                    if (itemData != null) {
                        recylerCallback.callback("details",itemData,postion)
                    }

                }

            }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_split_row, parent, false
                    )
                )
            }
        }
    }
}