package ru.netology.nmedia.adapters

import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.numberToString
import java.util.Date

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: PostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = Date(post.published*1000L).toString()
            content.text = post.content
            likeIcon.text = numberToString(post.likes)
            shareIcon.text = numberToString(post.sharesCount)
            viewIcon.text = numberToString(post.viewsCount)
            likeIcon.isChecked = post.likedByMe
            likeIcon.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareIcon.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            videoUrl.isVisible = post.videoUrl.isNotBlank()

            menuButton.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
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

            videoUrl.setOnClickListener {
                onInteractionListener.onViewVideo(post)
            }

            cardPostView.setOnClickListener {
                onInteractionListener.onViewPost(post)
            }
        }
    }
}
