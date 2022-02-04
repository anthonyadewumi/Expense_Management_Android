package com.bonhams.expensemanagement.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.SplitClaimItem
import com.bonhams.expensemanagement.databinding.ItemSplitDetalisRowBinding
import com.bonhams.expensemanagement.databinding.ItemSplitRowBinding
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimDetalisActivity
import com.bonhams.expensemanagement.utils.RecylerCallback


class SplitDetailsAdapterReadOnly(
    var isEditable :Boolean, var currencyCode :String ,var currencySymbol: String ,var splitList: MutableList<SplitClaimItem?>,var mcontext:Context,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<SplitDetailsAdapterReadOnly.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = splitList.get(position)
        attachmentItem?.let { holder.bindItems(isEditable,position,it,currencyCode,currencySymbol,recylerCallback) }

            holder.removeItems(splitList, position,recylerCallback)
            holder.details(splitList, position,mcontext,currencyCode,currencySymbol,recylerCallback)

    }
    override fun getItemCount(): Int {
        splitList?.let {
            return splitList?.size!!
        }
    }

    class ViewHolder(itemBinding: ItemSplitDetalisRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemSplitDetalisRowBinding = itemBinding
        fun bindItems(isEditable :Boolean,postion:Int,item: SplitClaimItem, currencyCode :String , currencySymbol: String ,recylerCallback: RecylerCallback) {
            binding.tvTitles.text = item.compnyName
            binding.tvCurrencySymbol.text = currencySymbol
           // binding.tvamount.setLocale(currencyCode)

                   binding.tvamount.isEnabled=false

                binding.tvamount.setText(String.format("%.2f",item.totalAmount.toDouble()))


            binding.tvTitle.text = item.expenceTypeName

            //  binding.tvamount.text = currencySymbol+item?.totalAmount
            binding.tvtax.text = "Tax : "+currencySymbol+item?.tax
            binding.tvtaxcode.text = "Tax Code : "+item?.taxcode

           /* binding.tvamount.doAfterTextChanged {
                var textamount =binding.tvamount.getNumericValue()
                println("chk total amount in details :"+textamount)

                if (textamount != null) {
                        val amount = textamount.toString().toDouble()
                        recylerCallback.callback("update", amount, postion)

                }

            }*/



        }


        fun removeItems(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,recylerCallback: RecylerCallback) {
                binding.ivDeleteSplit.setOnClickListener {
                    val amount= listAttachments?.get(postion)?.totalAmount.toString().toDouble()
                    listAttachments?.removeAt(postion)
                    bindingAdapter?.notifyDataSetChanged()
                    recylerCallback.callback("remove",amount,postion)
                }

            }
        fun details(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,context:Context,currencyCode :String , currencySymbol: String ,recylerCallback: RecylerCallback) {
            val itemData=listAttachments?.get(postion)
                binding.tvTitle.setOnClickListener {
                    if (itemData != null) {
                        recylerCallback.callback("details",itemData,postion)
                    }
                  /*  val splitItem = SplitClaimItem(
                        itemData?.companyNumber?:"0", itemData?.companyCode?:"0", itemData?.department?:"0", itemData?.expenseType?:"0",
                        itemData?.totalAmount?:"0", itemData?.taxcode?:"0",itemData?.tax?:0.0,itemData?.compnyName?:"0",itemData?.departmentName?:"0",
                        itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0"
                    )
                    val intent = Intent(context, SplitClaimDetalisActivity::class.java)
                    intent.putExtra("SplitItem", splitItem)
                    intent.putExtra("currencyCode", currencyCode)
                    intent.putExtra("currencySymbol", currencySymbol)
                    context.  startActivity(intent)*/

                }

            }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_split_detalis_row, parent, false
                    )
                )
            }
        }
    }
}