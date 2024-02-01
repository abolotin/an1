package ru.netology.nmedia.adapters

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.numberToString

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: PostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text=post.author
            published.text=post.published
            content.text=post.content
            likesText.text= numberToString(post.likesCount)
            sharesText.text=numberToString(post.sharesCount)
            viewsText.text=numberToString(post.viewsCount)
            likeIcon.setImageResource(if (post.likedByMe) R.drawable.ic_liked_24 else R.drawable.ic_like_24)
            likeIcon.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareIcon.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            menuButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener {item ->
                        when (item.itemId) {
                            R.id.postRemoveMenuItem -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.postEditMenuItem -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}
