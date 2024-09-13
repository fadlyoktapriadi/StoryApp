package com.example.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.response.ListStoryItem
import com.example.storyapp.databinding.ItemStoryBinding
import com.example.storyapp.ui.DetailStoryActivity

class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
        Glide.with(holder.imgstory.getContext())
            .load(story.photoUrl)
            .into(holder.imgstory);
        holder.itemView.setOnClickListener {
            val intentDetailStory =
                Intent(holder.itemView.context, DetailStoryActivity::class.java)
            intentDetailStory.putExtra(IMAGE_STORY, story.photoUrl)
            intentDetailStory.putExtra(TITLE_STORY, story.name)
            intentDetailStory.putExtra(DESC_STORY, story.description)
            holder.itemView.context.startActivity(intentDetailStory)
        }
    }

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val imgstory: ImageView = itemView.findViewById(R.id.imgStory)
        fun bind(story: ListStoryItem) {
            binding.titleStory.text = "${story.name}"
            binding.descStory.text = "${story.description}"
        }
    }

    companion object {

        const val IMAGE_STORY = "IMAGE_STORY"
        const val TITLE_STORY = "TITLE_STORY"
        const val DESC_STORY = "DESC_STORY"

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}
