package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.*
import com.bonhams.expensemanagement.data.services.responses.TotalClaimedData

class CustomSpinnerAdapter (context: Context, var mResource: Int, var dataSource: List<Any>) :
    ArrayAdapter<Any>(context, mResource, dataSource) {

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
        when (val item = dataSource[position]) {
            is ExpenseGroup -> {
                title.text = item.name.replaceFirstChar(Char::uppercase)
            }
            is ExpenseType -> {
                title.text = item.name.replaceFirstChar(Char::uppercase)
            }
            is MileageType -> {
                title.text = item.type.replaceFirstChar(Char::uppercase)
            }
            is Department -> {
                title.text = item.cost_code
            }
            is Currency -> {
                title.text = item.name.replaceFirstChar(Char::uppercase)
            }
            is Company -> {
                title.text = item.code
            }
            is StatusType -> {
                title.text = item.status.replaceFirstChar(Char::uppercase)
            }
            is UserType -> {
                title.text = item.type.replaceFirstChar(Char::uppercase)
            }
            is CarType -> {
                title.text = item.type.replaceFirstChar(Char::uppercase)
            }
            is Country -> {
                title.text = item.countryName.replaceFirstChar(Char::uppercase)
            }
            is Tax -> {
                title.text = item.tax_code.replaceFirstChar(Char::uppercase)
            }
            is TotalClaimedData -> {
                title.text = item.currency_type
            }
            is String -> {
                title.text = item.replaceFirstChar(Char::uppercase)
            }
        }

        return view
    }
}