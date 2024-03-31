package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.PostViewHolder
import ru.netology.nmedia.databinding.FragmentPostViewBinding
import ru.netology.nmedia.domain.PostInteractionListenerAbstract
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg

class PostViewFragment : Fragment() {
    companion object {
        var Bundle.postId: Long? by LongArg
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
            binding.included,
            object : PostInteractionListenerAbstract(viewModel) {
                override fun getContext(): Context? = context
                override fun getNavController() = findNavController()
                override fun getNavEdit() = R.id.action_postViewFragment_to_postEditFragment
                override fun onRemove(post: Post) {
                    super.onRemove(post)
                    findNavController().navigateUp()
                }
            }
        )

        arguments?.postId?.let { postId ->
            viewModel.getById(postId)?.let {
                postViewHolder.bind(it)
            }
        }

        return binding.root
    }
}