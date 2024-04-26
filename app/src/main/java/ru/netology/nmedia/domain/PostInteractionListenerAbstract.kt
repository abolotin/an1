package ru.netology.nmedia.domain

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.NavController
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostViewFragment.Companion.postId
import ru.netology.nmedia.dto.Post

abstract class PostInteractionListenerAbstract(
    private val viewModel: PostViewModel
) : PostInteractionListener {
    override fun onLike(post: Post) = viewModel.updateLike(post.id, post.likedByMe)

    override fun onShare(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, post.content)
        }

        val chooser = Intent.createChooser(
            intent,
            getContext()?.getString(R.string.intent_chooser_title)
        )
        getContext()?.startActivity(chooser)
        viewModel.shareById(post.id)
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
    }

    override fun onEdit(post: Post) {
        viewModel.editedPost = post
        getNavController().navigate(
            getNavEdit()
        )
    }

    override fun onViewVideo(post: Post) {
        if (post.videoUrl.isNotBlank()) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
            try {
                getContext()?.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(getContext(), e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    abstract fun getContext(): Context?
    abstract fun getNavController(): NavController
    abstract fun getNavEdit(): Int
}