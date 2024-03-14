package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.util.LongArg

class PostEditFragment : Fragment() {
    companion object {
        var Bundle.postId: Long? by LongArg
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

        val post = viewModel.getById(arguments?.postId ?: 0) ?: viewModel.getNewPost()

        binding.postContentEditor.setText(post.content)
        binding.postVideoUrlEditor.setText(post.videoUrl)

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
            findNavController().navigateUp()
        }

        return binding.root
    }
}