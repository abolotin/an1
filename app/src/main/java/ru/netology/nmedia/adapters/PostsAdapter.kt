package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id && oldItem.localId == newItem.localId

    override fun areContentsTheSame(oldItem: Post, newItem: Post) = oldItem == newItem
}
class PostsAdapter(
    private val onInteractionListener: PostInteractionListener,
    private val appAuth: AppAuth
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding = binding,
            appAuth = appAuth,
            onInteractionListener = onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post?.let { holder.bind(it) }
    }
}
