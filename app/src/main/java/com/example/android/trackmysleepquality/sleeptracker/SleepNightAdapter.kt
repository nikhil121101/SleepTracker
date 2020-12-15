package com.example.android.trackmysleepquality.sleeptracker

import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.TextItemViewBinding
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

// taking clickListener object from fragment so as to whatever has to be done on click stays
// independent from viewAdapter and controllable from fragment and vieModel

const val ITEM_TYPE_CODE = 1
const val HEADER_TYPE_CODE = 0

class SleepNightAdapter(private val clickListener : SleepClickListener) : ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            ITEM_TYPE_CODE -> ItemViewHolder.getObjectFrom(parent)
            HEADER_TYPE_CODE -> HeaderViewHolder.getObjectFrom(parent)
            else -> throw ClassCastException("Invalid viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            // not plying with content of HeaderViewHolder
            is ItemViewHolder -> {
                val night = getItem(position) as DataItem.SleepNightItem
                holder.bind(night.sleepNight , clickListener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)) {
            is DataItem.Header -> HEADER_TYPE_CODE
            is DataItem.SleepNightItem -> ITEM_TYPE_CODE
        }
    }

    fun updateDataItems(data : List<SleepNight>?) {
        CoroutineScope(Dispatchers.Default).launch {
            val items = when(data) {
                null -> listOf(DataItem.Header())
                else -> listOf(DataItem.Header()) + data.map { DataItem.SleepNightItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

}

class ItemViewHolder private constructor (val binding: TextItemViewBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(night : SleepNight , clickListener : SleepClickListener) {
        binding.sleepNight = night
        binding.clickListener = clickListener
        binding.executePendingBindings()
    }

    companion object {

        fun getObjectFrom(parent: ViewGroup): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TextItemViewBinding.inflate(layoutInflater , parent , false)
            return ItemViewHolder(binding)
        }
    }
}

class HeaderViewHolder private constructor (view : View): RecyclerView.ViewHolder(view) {

    companion object {
        fun getObjectFrom(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.header , parent , false)
            return HeaderViewHolder(view)
        }
    }

}

class SleepNightDiffCallback :
        DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }

}

sealed class DataItem {

    abstract val nightId : Long

    data class SleepNightItem(val sleepNight : SleepNight) : DataItem() {
        override val nightId = sleepNight.nightId
    }

    class Header : DataItem() {
        override val nightId = -1L
    }

}

class SleepClickListener(val doThis : (nightId : Long) -> Unit) {
    fun onClick(night : SleepNight) = doThis(night.nightId)
}