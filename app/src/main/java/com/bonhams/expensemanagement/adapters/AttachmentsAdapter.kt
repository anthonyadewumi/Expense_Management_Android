package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.databinding.ItemAttachmentBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class AttachmentsAdapter(
    var listAttachments: List<String?>?
) : RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = listAttachments?.get(position)
        attachmentItem?.let { holder.bindItems(it) }
    }

    override fun getItemCount(): Int {
        listAttachments?.let {
            return listAttachments?.size!!
        } ?: kotlin.run {
            return 0
        }
    }

    class ViewHolder(itemBinding: ItemAttachmentBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemAttachmentBinding = itemBinding

        fun bindItems(item: String) {
            Glide.with(itemView.context)
                .load(item)
                .apply(
                    RequestOptions()
                    .placeholder(R.drawable.ic_default_media)
                    .error(R.drawable.ic_default_media)
                )
                .placeholder(R.drawable.ic_default_media)
                .error(R.drawable.ic_default_media)
                .into(binding.ivAttachment);
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_attachment, parent, false
                    )
                )
            }
        }
    }
}