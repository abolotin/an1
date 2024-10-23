package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem) : Boolean {
        if (oldItem is Post && newItem is Post) return oldItem.id == newItem.id && oldItem.localId == newItem.localId
        return false
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem) = oldItem == newItem
}
class PostsAdapter(
    private val onInteractionListener: PostInteractionListener,
    private val appAuth: AppAuth
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Post -> R.layout.card_post
            null -> error("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(
                    binding = binding,
                    appAuth = appAuth,
                    onInteractionListener = onInteractionListener)
            }
            else -> error("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)) {
            is Post -> (holder as? PostViewHolder)?.bind(item)
            else -> error("Unknown view type")
        }
    }
}

class PostsLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<PostsLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PostsLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PostsLoadStateViewHolder {
        return PostsLoadStateViewHolder.create(parent, retry)
    }
}