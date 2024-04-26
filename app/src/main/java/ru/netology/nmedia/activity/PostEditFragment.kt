package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.entity.PostEditState
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
        val post = viewModel.editedPost

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (post.id == 0L) {
                    with(binding) {
                        draftContent = postContentEditor.text.toString()
                        draftVideoUrl = postVideoUrlEditor.text.toString()
                    }
                }
                isEnabled = false
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

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
        }

        binding.errorOkButton.setOnClickListener {
            viewModel.editState.postValue(PostEditState(status = PostEditState.Status.OK))
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            // Навигация назад
            draftContent = ""
            draftVideoUrl = ""
            callback.isEnabled = false
            viewModel.loadPosts()
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        viewModel.editState.observe(viewLifecycleOwner) {
            binding.errorGroup.isVisible = it.isError
            binding.progressBar.isVisible = it.isSaving
            binding.ok.isEnabled = it.isOk
            binding.postContentEditor.isEnabled = it.isOk
            binding.postVideoUrlEditor.isEnabled = it.isOk
        }

        return binding.root
    }
}