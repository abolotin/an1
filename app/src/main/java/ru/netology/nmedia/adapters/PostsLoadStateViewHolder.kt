package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.PostLoadStateBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.numberToString
import java.util.Date

class PostsLoadStateViewHolder constructor (
    private val binding: PostLoadStateBinding,
    retry: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PostsLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.post_load_state, parent, false)
            val binding = PostLoadStateBinding.bind(view)
            return PostsLoadStateViewHolder(binding, retry)
        }
    }}
