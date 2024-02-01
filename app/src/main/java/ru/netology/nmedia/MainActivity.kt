package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import ru.netology.nmedia.adapters.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter(
            object : PostInteractionListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.shareById(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.editedPost.value = post
                    with (binding.postTextEditor) {
                        clearFocus()
                        hideKeyboard(this)
                    }
                }
            }
        )

        binding.list.adapter = adapter

        binding.postSaveButton.setOnClickListener {
            with(binding.postTextEditor) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        applicationContext.resources.getString(R.string.empty_content_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // val postId = viewModel.editedPost.value?.id
                viewModel.updateContent(text.toString())
                viewModel.save()
                setText("")
                clearFocus()
                hideKeyboard(binding.postTextEditor)
            }
        }

        binding.postEditCancelButton.setOnClickListener {
            viewModel.cancelEdit()
            binding.postTextEditor.setText("")
            hideKeyboard(binding.postTextEditor)
        }

        viewModel.data.observe(this) { posts ->
            val scrollToStart =
                adapter.currentList.size > 0 && (posts.size != adapter.currentList.size);
            adapter.submitList(posts)
            if (scrollToStart) binding.list.smoothScrollToPosition(0)
        }

        viewModel.editedPost.observe(this) { editedPost ->
            if (editedPost.id == 0L) {
                if (binding.postEditGroup.isVisible) {
                    binding.postEditGroup.visibility = View.GONE
                }
                hideKeyboard(binding.postTextEditor)
                return@observe
            }

            with(binding) {
                if (!postEditGroup.isVisible) {
                    postEditGroup.visibility = View.VISIBLE
                }
                postEditPrevText.text = editedPost.content
                postTextEditor.setText(editedPost.content)
                postTextEditor.focusAndShowKeyboard();
            }
        }
    }
}