package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.databinding.MaterialPageBinding
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ImagePager2Adapter(var context: Context,var recylerCallback: RecylerCallback,var attachmentList: ArrayList<String>
) : RecyclerView.Adapter<ImagePager2Adapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.material_page,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemImage.setOnClickListener {
            recylerCallback.callback("show",attachmentList[position],position)

        }
        Glide.with(context)
            .load(attachmentList[position])
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.mountains)
                    .error(R.drawable.mountains)
            )
            .placeholder(R.drawable.mountains)
            .error(R.drawable.mountains)
            .into(holder.binding.itemImage)
    }

    override fun getItemCount() = attachmentList.size


    inner class ViewHolder(val binding: MaterialPageBinding) :
        RecyclerView.ViewHolder(binding.root)


}