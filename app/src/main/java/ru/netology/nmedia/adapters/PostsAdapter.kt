package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}
class PostsAdapter(
    private val onInteractionListener: PostInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        var binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}
