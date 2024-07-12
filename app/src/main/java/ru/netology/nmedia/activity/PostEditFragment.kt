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
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.entity.PostEditState
import ru.netology.nmedia.util.LongArg

class PostEditFragment : Fragment() {
    companion object {
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

        viewModel.postSaved.observe(viewLifecycleOwner) {
            // Навигация назад
            draftContent = ""
            draftVideoUrl = ""
            callback.isEnabled = false
            // viewModel.loadPosts()
            viewModel.updatePostItem(it)
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        viewModel.editState.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it.isSaving
            binding.ok.isEnabled = !it.isSaving
            binding.postContentEditor.isEnabled = !it.isSaving
            binding.postVideoUrlEditor.isEnabled = !it.isSaving
            if (it.isError) {
                Snackbar.make(binding.root, R.string.edit_saving_error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_button) {
                        binding.ok.performClick()
                    }
                    .show()
            }
        }

        return binding.root
    }
}