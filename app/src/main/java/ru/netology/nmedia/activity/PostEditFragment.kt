package ru.netology.nmedia.activity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostEditBinding
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.entity.PhotoModel
import ru.netology.nmedia.entity.PostEntity

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
                viewModel.updatePhoto(null, null)
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                // HINT: Слетает обработчик back после возврата из интента. Восстанавливаем его
                requireActivity().onBackPressedDispatcher.addCallback(callback)

                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri = it.data?.data
                        viewModel.updatePhoto(uri, uri?.toFile())
                    }
                }
            }

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.save -> {
                            val content = binding.postContentEditor.text.toString()
                            val videoUrl = binding.postVideoUrlEditor.text.toString()
                            if (content.isNotBlank() || videoUrl.isNotBlank()) {
                                viewModel.savePost(
                                    post.copy(
                                        localId = if (post.localId == 0L) PostEntity.nextLocalId else post.localId,
                                        content = content,
                                        videoUrl = videoUrl
                                    )
                                )
                                // Навигация назад
                                draftContent = ""
                                draftVideoUrl = ""
                                callback.isEnabled = false
                                viewModel.updatePhoto(null, null)
                                findNavController().navigateUp()
                            }
                            true
                        }

                        else -> false
                    }
                }
            },
            viewLifecycleOwner
        )

        if (post.id == 0L && post.localId == 0L) {
            binding.postContentEditor.setText(draftContent)
            binding.postVideoUrlEditor.setText(draftVideoUrl)
        } else {
            binding.postContentEditor.setText(post.content)
            binding.postVideoUrlEditor.setText(post.videoUrl)
        }

        binding.pickPhotoButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .galleryOnly()
                .compress(2048)
                .createIntent(pickPhotoLauncher::launch)
        }
        binding.pickCameraButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .cameraOnly()
                .compress(2048)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.removeImagePreviewButton.setOnClickListener {
            viewModel.updatePhoto(null, null)
        }

        requireActivity().onBackPressedDispatcher.addCallback(callback)

        viewModel.photo.observe(viewLifecycleOwner) {
            binding.imagePreviewLayout.isVisible = (!it.isEmpty)
            binding.imagePreview.setImageURI(it.uri)
        }

        return binding.root
    }
}