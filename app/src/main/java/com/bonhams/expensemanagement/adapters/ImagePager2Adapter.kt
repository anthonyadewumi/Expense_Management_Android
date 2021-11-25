package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.databinding.MaterialPageBinding
import com.bonhams.expensemanagement.utils.RecylerCallback

class ImagePager2Adapter(var context: Context,var recylerCallback: RecylerCallback
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
            recylerCallback.callback("show","Image",position)

        }

    }

    override fun getItemCount() = 3


    inner class ViewHolder(val binding: MaterialPageBinding) :
        RecyclerView.ViewHolder(binding.root)


}