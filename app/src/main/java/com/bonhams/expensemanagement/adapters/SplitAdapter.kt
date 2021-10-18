package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.SplitClaimDetail
import com.bonhams.expensemanagement.data.model.SplitClaimItem
import com.bonhams.expensemanagement.databinding.ItemAttachmentBinding
import com.bonhams.expensemanagement.databinding.ItemSplitRowBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class SplitAdapter(
    var splitList: MutableList<SplitClaimItem?>,
) : RecyclerView.Adapter<SplitAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = splitList?.get(position)
        attachmentItem?.let { holder.bindItems(it) }

            holder.removeItems(splitList, position)

    }
    override fun getItemCount(): Int {
        splitList?.let {
            return splitList?.size!!
        }
    }

    class ViewHolder(itemBinding: ItemSplitRowBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemSplitRowBinding = itemBinding
        fun bindItems(item: SplitClaimItem) {
           binding.tvTitle.setText(item?.companyCode)
           binding.tvamount.setText("Amount : $ "+item?.totalAmount)
           binding.tvtax.setText("Tax : $ "+item?.tax)
           binding.tvtaxcode.setText("Tax Code : "+item?.taxcode)
        }

        fun removeItems(listAttachments: MutableList<SplitClaimItem?>?,postion:Int) {
                binding.tvCreateCopy.setOnClickListener {
                    listAttachments?.removeAt(postion)
                    bindingAdapter?.notifyDataSetChanged()
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