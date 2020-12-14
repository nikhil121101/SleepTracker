package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.TextItemViewBinding
class SleepNightAdapter : ListAdapter<SleepNight, ItemViewHolder>(SleepNightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder.getObjectFrom(parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val night = getItem(position)
        holder.binding.sleepNight = night
    }

}

class ItemViewHolder private constructor (val binding: TextItemViewBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun getObjectFrom(parent: ViewGroup): ItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TextItemViewBinding.inflate(layoutInflater , parent , false)
            return ItemViewHolder(binding)
        }
    }
}

class SleepNightDiffCallback :
        DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}