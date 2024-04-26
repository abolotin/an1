package ru.netology.nmedia.activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostViewFragment.Companion.postId
import ru.netology.nmedia.adapters.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.domain.PostInteractionListenerAbstract
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.FeedState

class FeedFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by activityViewModels()

        val adapter = PostsAdapter(
            object : PostInteractionListenerAbstract(viewModel) {
                override fun getContext(): Context? = context
                override fun getNavController() = findNavController()
                override fun getNavEdit() = R.id.action_feedFragment_to_postEditFragment
                override fun onViewPost(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_postViewFragment,
                        Bundle().apply {
                            postId = post.id
                        }
                    )
                }
            }
        )

        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        )

        binding.list.adapter = adapter

        binding.add.setOnClickListener {
            viewModel.editedPost = viewModel.getNewPost()
            findNavController().navigate(R.id.action_feedFragment_to_postEditFragment)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        viewModel.feedState.observe(viewLifecycleOwner) { feedState ->
            binding.progressBar.isVisible = feedState.isLoading
            binding.errorGroup.isVisible = feedState.isError
            binding.emptyListTitle.isVisible = feedState.isEmpty
            binding.swiper.isRefreshing = feedState.isLoading;
            adapter.submitList(feedState.posts)
        }

        binding.swiper.setOnRefreshListener {
            viewModel.loadPosts()
        }

        return binding.root
    }
}