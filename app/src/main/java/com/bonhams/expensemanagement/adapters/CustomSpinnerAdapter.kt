package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.SpinnerItem

class CustomSpinnerAdapter (context: Context, var mResource: Int, var dataSource: List<SpinnerItem>) :
    ArrayAdapter<SpinnerItem>(context, mResource, dataSource) {

    private val mInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent);
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }


    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view: View = mInflater.inflate(mResource, parent, false)
        val title = view.findViewById(R.id.title) as TextView
        val item: SpinnerItem = dataSource[position]
        title.text = item.title.replaceFirstChar(Char::uppercase)

        return view
    }
}