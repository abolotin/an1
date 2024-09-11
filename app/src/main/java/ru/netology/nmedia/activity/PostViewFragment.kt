package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.PostViewHolder
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentPostViewBinding
import ru.netology.nmedia.domain.PostInteractionListenerAbstract
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg
import javax.inject.Inject

@AndroidEntryPoint
class PostViewFragment : Fragment() {
    @Inject
    lateinit var appAuth: AppAuth

    companion object {
        var Bundle.postId: Long? by LongArg
        var Bundle.postLocalId: Long? by LongArg
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostViewBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by activityViewModels()
        val postViewHolder = PostViewHolder(
            binding = binding.included,
            appAuth = appAuth,
            onInteractionListener =  object : PostInteractionListenerAbstract(viewModel) {
                override fun getContext(): Context? = context
                override fun getNavController() = findNavController()
                override fun getNavEdit() = R.id.action_postViewFragment_to_postEditFragment
                override fun onRemove(post: Post) {
                    super.onRemove(post)
                    findNavController().navigateUp()
                }
                override fun onViewPhoto(post: Post) {
                    findNavController().navigate(
                        R.id.action_postViewFragment_to_photoViewFragment,
                        Bundle().apply {
                            postId = post.id
                            postLocalId = post.localId
                        }
                    )
                }
            }
        )

        arguments?.postId?.let { postId ->
            arguments?.postLocalId?.let { localId ->
                viewModel.data.observe(viewLifecycleOwner) { data ->
                    data.posts.filter { it.id == postId && it.localId == localId }.firstOrNull()
                        ?.let { postViewHolder.bind(it) }
                }
            }
        }

        return binding.root
    }
}