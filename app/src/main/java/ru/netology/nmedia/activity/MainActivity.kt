package ru.netology.nmedia.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.view.isVisible
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.AndroidUtils.focusAndShowKeyboard
import ru.netology.nmedia.util.AndroidUtils.hideKeyboard

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val editPostLauncher = registerForActivityResult(EditPostActivityContract) {
            it ?: return@registerForActivityResult
            viewModel.savePost(it)
        }

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

                    val chooser = Intent.createChooser(intent,
                        getString(R.string.intent_chooser_title))
                    startActivity(chooser)
                    //viewModel.shareById(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    editPostLauncher.launch(post)
                }
            }
        )

        binding.list.adapter = adapter

        binding.add.setOnClickListener {
            editPostLauncher.launch(viewModel.getNewPost())
        }

        viewModel.data.observe(this) { posts ->
            val scrollToStart =
                adapter.currentList.size > 0 && (posts.size != adapter.currentList.size);
            adapter.submitList(posts)
            if (scrollToStart) binding.list.smoothScrollToPosition(0)
        }
    }
}
