package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.PostViewFragment.Companion.postId
import ru.netology.nmedia.adapters.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post

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
            object : PostInteractionListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, post.content)
                    }

                    val chooser = Intent.createChooser(
                        intent,
                        getString(R.string.intent_chooser_title)
                    )
                    startActivity(chooser)
                    //viewModel.shareById(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    findNavController().navigate(
                        R.id.action_feedFragment_to_postEditFragment,
                        Bundle().apply {
                            postId = post.id
                        }
                    )
                }

                override fun onViewVideo(post: Post) {
                    if (post.videoUrl.isNotBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

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
            findNavController().navigate(R.id.action_feedFragment_to_postEditFragment)
        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        return binding.root
    }
}