package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_attachment.view.*


class AttachmentsAdapter(
    var listAttachments: List<String?>?
) : RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attachment, parent, false)
        return ViewHolder(v)
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                .into(itemView.ivAttachment);
        }
    }
}