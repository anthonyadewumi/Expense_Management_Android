package com.bonhams.expensemanagement.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.NavDrawerItem
import kotlinx.android.synthetic.main.item_nav_drawer.view.*

class NavDrawerAdapter (private var items: ArrayList<NavDrawerItem>, private var currentPos: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context
    private val ITEM_VIEW_TYPE_HEADER = 1
    private val ITEM_VIEW_TYPE_ITEM = 2

    class NavigationItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class NavigationHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                context = parent.context
                val navItem = LayoutInflater.from(parent.context).inflate(R.layout.item_nav_drawer_header, parent, false)
                return NavigationHeaderViewHolder(navItem)
            }
            else -> {
                context = parent.context
                val navItem = LayoutInflater.from(parent.context).inflate(R.layout.item_nav_drawer, parent, false)
                return NavigationItemViewHolder(navItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].code) {
            -1 -> ITEM_VIEW_TYPE_HEADER
            else -> ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NavigationHeaderViewHolder -> {
                holder.itemView.navigation_title.text = items[position].title
                holder.itemView.navigation_title.setTextColor(Color.WHITE)
            }
            else -> {
                if (position == currentPos) {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
                } else {
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
                }
                holder.itemView.navigation_icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                holder.itemView.navigation_title.setTextColor(Color.WHITE)
                holder.itemView.navigation_title.text = items[position].title
                holder.itemView.navigation_icon.setImageResource(items[position].icon)
            }
        }
    }
}