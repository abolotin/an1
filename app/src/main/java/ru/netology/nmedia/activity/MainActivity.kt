package ru.netology.nmedia.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import ru.netology.nmedia.R
import ru.netology.nmedia.adapters.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.domain.PostInteractionListener
import ru.netology.nmedia.domain.PostViewModel
import ru.netology.nmedia.dto.Post

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

                override fun onViewVideo(post: Post) {
                    if (! post.videoUrl.isNullOrBlank()) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        )

        adapter.registerAdapterDataObserver(
            object : AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        )

        binding.list.adapter = adapter

        binding.add.setOnClickListener {
            editPostLauncher.launch(viewModel.getNewPost())
        }

        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
    }
}
