package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.LongArg

class PostEditFragment : Fragment() {
    companion object {
        var Bundle.postId: Long? by LongArg
        var draftContent: String = ""
        var draftVideoUrl: String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPostEditBinding.inflate(
            inflater,
            container,
            false
        )

        val viewModel: PostViewModel by activityViewModels()
        val postLiveData = MutableLiveData<Post?>()
        val postId = arguments?.postId ?: 0L

        if (postId == 0L) {
            postLiveData.value = viewModel.getNewPost()
        } else {
            viewModel.getById(postId)
                .observe(viewLifecycleOwner) { post ->
                    postLiveData.value = post ?: viewModel.getNewPost()
                }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (postId == 0L) {
                    with(binding) {
                        draftContent = postContentEditor.text.toString()
                        draftVideoUrl = postVideoUrlEditor.text.toString()
                    }
                }
                isEnabled = false
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        postLiveData.observe(viewLifecycleOwner) { post ->
            if (post == null) {
                findNavController().navigateUp()
            } else {
                if (post.id == 0L) {
                    binding.postContentEditor.setText(draftContent)
                    binding.postVideoUrlEditor.setText(draftVideoUrl)
                } else {
                    binding.postContentEditor.setText(post.content)
                    binding.postVideoUrlEditor.setText(post.videoUrl)
                }

                binding.ok.setOnClickListener {
                    val content = binding.postContentEditor.text.toString()
                    val videoUrl = binding.postVideoUrlEditor.text.toString()
                    if (content.isNotBlank() || videoUrl.isNotBlank()) {
                        viewModel.savePost(
                            post.copy(
                                content = content,
                                videoUrl = videoUrl
                            )
                        )
                    }
                    // Навигация назад
                    draftContent = ""
                    draftVideoUrl = ""
                    callback.isEnabled = false
                    findNavController().navigateUp()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        return binding.root
    }
}