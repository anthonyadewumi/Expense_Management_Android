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
import com.bonhams.expensemanagement.data.model.SplitClaimDetail
import com.bonhams.expensemanagement.data.model.SplitClaimItem
import com.bonhams.expensemanagement.databinding.ItemAttachmentBinding
import com.bonhams.expensemanagement.databinding.ItemSplitRowBinding
import com.bonhams.expensemanagement.ui.claims.newClaim.SplitClaimDetalisActivity
import com.bonhams.expensemanagement.ui.claims.splitClaim.SplitClaimFragment.Companion.totalAmount
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class SplitAdapter(
    var splitList: MutableList<SplitClaimItem?>,var mcontext:Context,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<SplitAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = splitList?.get(position)
        attachmentItem?.let { holder.bindItems(it) }

            holder.removeItems(splitList, position,recylerCallback)
            holder.details(splitList, position,mcontext)

    }
    override fun getItemCount(): Int {
        splitList?.let {
            return splitList?.size!!
        }
    }

    class ViewHolder(itemBinding: ItemSplitRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemSplitRowBinding = itemBinding
        fun bindItems(item: SplitClaimItem) {
           binding.tvTitles.setText(item?.compnyName)
           binding.tvTitle.setText(item?.expenceTypeName)
           binding.tvamount.setText(" $ "+item?.totalAmount)
           binding.tvtax.setText("Tax : $ "+item?.tax)
           binding.tvtaxcode.setText("Tax Code : "+item?.taxcode)
        }

        fun removeItems(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,recylerCallback: RecylerCallback) {
                binding.ivDeleteSplit.setOnClickListener {
                    val amount= listAttachments?.get(postion)?.totalAmount.toString().toDouble()
                    listAttachments?.removeAt(postion)
                    bindingAdapter?.notifyDataSetChanged()
                    recylerCallback.callback("remove",amount,postion)
                }

            }
        fun details(listAttachments: MutableList<SplitClaimItem?>?,postion:Int,context:Context) {
            val itemData=listAttachments?.get(postion)
                binding.tvTitle.setOnClickListener {
                    val splitItem = SplitClaimItem(
                        itemData?.companyNumber?:"0", itemData?.companyCode?:"0", itemData?.department?:"0", itemData?.expenseType?:"0",
                        itemData?.totalAmount?:"0", itemData?.taxcode?:"0",itemData?.tax?:0.0,itemData?.compnyName?:"0",itemData?.departmentName?:"0",
                        itemData?.expenceTypeName?:"0",itemData?.auctionSales?:"0",itemData?.expenceCode?:"0"
                    )
                    val intent = Intent(context, SplitClaimDetalisActivity::class.java)
                    intent.putExtra("SplitItem", splitItem)
                    context.  startActivity(intent)

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