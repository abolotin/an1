package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPhotoViewBinding
import ru.netology.nmedia.domain.PostInteractionListenerAbstract
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.numberToString
import javax.inject.Inject

@AndroidEntryPoint
class PhotoViewFragment : Fragment() {
    @Inject
    lateinit var repository: PostRepository

    companion object {
        var Bundle.postId: Long? by LongArg
        var Bundle.postLocalId: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoViewBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by activityViewModels()
        val interactionListener = object : PostInteractionListenerAbstract(viewModel) {
            override fun getContext(): Context? = context
            override fun getNavController() = findNavController()
            override fun getNavEdit() = R.id.action_postViewFragment_to_postEditFragment
        }
        arguments?.postId?.let { postId ->
            arguments?.postLocalId?.let { localId ->
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                        repository.getLocalOne(postId, localId)?.let { post ->
                            with(binding) {
                                likeIcon.text = numberToString(post.likes)
                                shareIcon.text = numberToString(post.sharesCount)
                                shareIcon.isEnabled = post.isSaved
                                viewIcon.text = numberToString(post.viewsCount)
                                viewIcon.isEnabled = post.isSaved
                                likeIcon.isChecked = post.likedByMe
                                likeIcon.isEnabled = post.isSaved
                                likeIcon.setOnClickListener {
                                    likeIcon.isEnabled = false
                                    interactionListener.onLike(post)
                                }
                                shareIcon.setOnClickListener {
                                    interactionListener.onShare(post)
                                }
                            }
                            post.attachment?.let { attach ->
                                binding.photo.isVisible = true
                                Glide.with(binding.root)
                                    .load(BuildConfig.BASE_URL + "media/${attach.url}")
                                    .timeout(10000)
                                    .placeholder(R.drawable.ic_loading_100dp)
                                    .error(R.drawable.ic_error_100dp)
                                    .into(binding.photo)
                            }
                        }
                    }
                }
            }
        }

        /* arguments?.postId?.let { postId ->
            arguments?.postLocalId?.let { localId ->
                viewModel.data.asLiveData().observe(viewLifecycleOwner) { data ->
                    data.posts.filter { it.id == postId && it.localId == localId }.firstOrNull()
                        ?.let { post ->
                            with(binding) {
                                likeIcon.text = numberToString(post.likes)
                                shareIcon.text = numberToString(post.sharesCount)
                                shareIcon.isEnabled = post.isSaved
                                viewIcon.text = numberToString(post.viewsCount)
                                viewIcon.isEnabled = post.isSaved
                                likeIcon.isChecked = post.likedByMe
                                likeIcon.isEnabled = post.isSaved
                                likeIcon.setOnClickListener {
                                    likeIcon.isEnabled = false
                                    interactionListener.onLike(post)
                                }
                                shareIcon.setOnClickListener {
                                    interactionListener.onShare(post)
                                }
                            }
                            post.attachment?.let { attach ->
                                binding.photo.isVisible = true
                                Glide.with(binding.root)
                                    .load(BuildConfig.BASE_URL + "media/${attach.url}")
                                    .timeout(10000)
                                    .placeholder(R.drawable.ic_loading_100dp)
                                    .error(R.drawable.ic_error_100dp)
                                    .into(binding.photo)
                            }

                        }
                }
            }
        } */

        return binding.root
    }
}