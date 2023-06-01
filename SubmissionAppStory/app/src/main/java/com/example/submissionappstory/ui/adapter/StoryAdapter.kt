package com.example.submissionappstory.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.submissionappstory.data.remote.apiresponse.ListStory
import com.example.submissionappstory.databinding.ItemRowBinding
import com.example.submissionappstory.ui.main.DetailStoryActivity
import com.example.submissionappstory.ui.util.setSafeOnClickListener
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter : PagingDataAdapter<ListStory, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(item: ListStory) {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS'Z'")
            dateFormat.timeZone = TimeZone.getTimeZone("ID")
            val time = dateFormat.parse(item.createdAt)?.time
            val prettyTime = PrettyTime(Locale.getDefault())
            val date = prettyTime.format(time?.let { Date(it) })

            with(binding) {
                tvName.text = item.name
                tvCreated.text = date
                tvDescription.text = item.description
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imgStory)
            }
            itemView.setSafeOnClickListener {
                with(it.context) {
                    val detailIntent = Intent(this, DetailStoryActivity::class.java)
                    detailIntent.putExtra(DETAIL, item)
                    startActivity(detailIntent)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: StoryAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapter.ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
        const val DETAIL = "detail"
    }
}