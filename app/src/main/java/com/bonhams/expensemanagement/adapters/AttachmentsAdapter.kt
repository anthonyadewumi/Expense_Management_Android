package com.bonhams.expensemanagement.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.databinding.ItemAttachmentBinding
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class AttachmentsAdapter(
    var listAttachments: MutableList<String?>,
    var callby:String,var recylerCallback: RecylerCallback
) : RecyclerView.Adapter<AttachmentsAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val attachmentItem = listAttachments?.get(position)
        attachmentItem?.let { holder.bindItems(it) }

            holder.removeItems(listAttachments, position,callby,recylerCallback)
            holder.showItems(listAttachments, position,callby,recylerCallback)

    }
    override fun getItemCount(): Int {
        listAttachments?.let {
            return listAttachments?.size!!
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
                        .override(600, 200)
                )
                .placeholder(R.drawable.ic_default_media)
                .error(R.drawable.ic_default_media)
                .into(binding.ivAttachment)
        }
        fun removeItems(listAttachments: MutableList<String?>?,postion:Int,callby:String,recylerCallback: RecylerCallback) {

            if(!callby.equals("detalis")) {
                binding.ivDeleteCross.visibility=View.VISIBLE

                binding.ivDeleteCross.setOnClickListener {
                    listAttachments?.removeAt(postion)
                    listAttachments?.size?.let { it1 ->
                        recylerCallback.callback("remove",
                            it1,postion)
                    }


                    System.out.println("remove listAttachments size :${listAttachments?.size}")

                    bindingAdapter?.notifyDataSetChanged()
                }
                }else{
                    binding.ivDeleteCross.visibility=View.GONE
                }
            }
                fun showItems(listAttachments: MutableList<String?>?,postion:Int,callby:String,recylerCallback: RecylerCallback) {


                    binding.ivAttachment.setOnClickListener {
                        listAttachments?.get(postion)?.let { it1 ->
                            recylerCallback.callback("show",
                                it1,position)
                        }

                    }

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