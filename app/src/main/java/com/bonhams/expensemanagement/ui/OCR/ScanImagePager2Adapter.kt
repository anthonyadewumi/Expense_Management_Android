package com.bonhams.expensemanagement.ui.OCR

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.databinding.MaterialPageBinding
import com.bonhams.expensemanagement.databinding.ScanPageItemBinding
import com.bonhams.expensemanagement.utils.RecylerCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.ArrayList

class ScanImagePager2Adapter(var context: Context, var recylerCallback: RecylerCallback, var attachmentList: ArrayList<Bitmap>
) : RecyclerView.Adapter<ScanImagePager2Adapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.scan_page_item,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.itemImage.setOnClickListener {
            recylerCallback.callback("show",attachmentList[position],position)

        }
        recylerCallback.callback("postion",attachmentList[position],position)
        holder.binding.itemImage.setImageBitmap(attachmentList[position])

    }

    override fun getItemCount() = attachmentList.size


    inner class ViewHolder(val binding: ScanPageItemBinding) :
        RecyclerView.ViewHolder(binding.root)


}