package ru.netology.nmedia.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
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
            likeIcon.text = numberToString(post.likesCount)
            shareIcon.text = numberToString(post.sharesCount)
            viewIcon.text = numberToString(post.viewsCount)
            likeIcon.isChecked = post.likedByMe
            likeIcon.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            shareIcon.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            if (post.videoUrl.isNotBlank())
                videoUrl.isVisible = true
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
            videoUrl.setOnClickListener() {
                if (post.videoUrl.isNullOrBlank())
                    return@setOnClickListener
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                try {
                    it.context.startActivity(intent)
                } catch(e: ActivityNotFoundException) {
                    Toast.makeText(it.context, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
