package com.bonhams.expensemanagement.adapters

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bonhams.expensemanagement.R
import com.bonhams.expensemanagement.data.model.NavDrawerItem
import com.bonhams.expensemanagement.databinding.ItemNavDrawerBinding
import com.bonhams.expensemanagement.databinding.ItemNavDrawerHeaderBinding

class NavDrawerAdapter (private var items: ArrayList<NavDrawerItem>, private var currentPos: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE_HEADER = 1
    private val ITEM_VIEW_TYPE_ITEM = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> {
                return NavigationHeaderViewHolder.create(parent)
            }
            else -> {
                return NavigationItemViewHolder.create(parent)
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
                holder.bindItems(items[position])
            }
            else -> {
                (holder as NavigationItemViewHolder).bindItems(items[position], position, currentPos)
            }
        }
    }

    class NavigationHeaderViewHolder(itemBinding: ItemNavDrawerHeaderBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemNavDrawerHeaderBinding = itemBinding

        fun bindItems(item: NavDrawerItem) {
            binding.navigationTitle.text = item.title
            binding.navigationTitle.setTextColor(Color.WHITE)
        }

        companion object {
            fun create(parent: ViewGroup): NavigationHeaderViewHolder {
                return NavigationHeaderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_nav_drawer_header, parent, false
                    )
                )
            }
        }
    }

    class NavigationItemViewHolder(itemBinding: ItemNavDrawerBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        private val binding: ItemNavDrawerBinding = itemBinding

        fun bindItems(item: NavDrawerItem, position: Int, currentPos: Int) {
            if (position == currentPos) {
                binding.navContainer.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.primary))
            } else {
                binding.navContainer.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
            }
            binding.navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
            binding.navigationIcon.setImageResource(item.icon)
            binding.navigationTitle.setTextColor(Color.WHITE)
            binding.navigationTitle.text = item.title
        }

        companion object {
            fun create(parent: ViewGroup): NavigationItemViewHolder {
                return NavigationItemViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_nav_drawer, parent, false
                    )
                )
            }
        }
    }
}