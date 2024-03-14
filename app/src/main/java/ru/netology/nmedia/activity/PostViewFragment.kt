package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostViewBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.util.LongArg
import ru.netology.nmedia.util.numberToString

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

        viewModel.data.observe(viewLifecycleOwner) { list ->
            list?.firstOrNull { it.id == arguments?.postId }?.let { post ->
                binding.apply {
                    included.author.text = post.author
                    included.published.text = post.published
                    included.content.text = post.content
                    included.likeIcon.text = numberToString(post.likesCount)
                    included.shareIcon.text = numberToString(post.sharesCount)
                    included.viewIcon.text = numberToString(post.viewsCount)
                    included.likeIcon.isChecked = post.likedByMe
                    included.videoUrl.isVisible = post.videoUrl.isNotBlank()

                    // TODO: Убрать дублирование кода с FeedFragment -> PostInteractionListener!
                    included.likeIcon.setOnClickListener {
                        viewModel.likeById(post.id)
                    }
                    included.shareIcon.setOnClickListener {
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
                    }
                    included.menuButton.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.post_options)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.postRemoveMenuItem -> {
                                        viewModel.removeById(post.id)
                                        findNavController().navigateUp()
                                        true
                                    }

                                    R.id.postEditMenuItem -> {
                                        findNavController().navigate(
                                            R.id.action_postViewFragment_to_postEditFragment,
                                            Bundle().apply {
                                                postId = post.id
                                            }
                                        )
                                        true
                                    }

                                    else -> false
                                }
                            }
                        }.show()
                    }
                    included.videoUrl.setOnClickListener {
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
                    // /TODO
                }
            }
        }

        return binding.root
    }
}