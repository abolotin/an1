package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.databinding.ActivityEditPostBinding
import ru.netology.nmedia.dto.Post

const val EXTRA_CONTENT = "content"
const val EXTRA_VIDEO_URL = "videoUrl"

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.postContentEditor.setText(intent?.getStringExtra(EXTRA_CONTENT) ?: "")
        binding.postVideoUrlEditor.setText(intent?.getStringExtra(EXTRA_VIDEO_URL) ?: "")

        binding.ok.setOnClickListener {
            val content = binding.postContentEditor.text.toString()
            val videoUrl = binding.postVideoUrlEditor.text.toString()
            if (content.isNotBlank() || videoUrl.isNotBlank()) {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(EXTRA_CONTENT, content)
                    putExtra(EXTRA_VIDEO_URL, videoUrl)
                })
            } else {
                setResult(RESULT_CANCELED)
            }
            finish()
        }
    }
}

object EditPostActivityContract : ActivityResultContract<Post, Post?>() {
    lateinit var post: Post

    override fun createIntent(context: Context, input: Post): Intent {
        post = input
        return Intent(context, EditPostActivity::class.java).apply {
            putExtra(EXTRA_CONTENT, input.content)
            putExtra(EXTRA_VIDEO_URL, input.videoUrl)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?) =
        if (resultCode == Activity.RESULT_OK) post.copy(
            content = intent?.getStringExtra(EXTRA_CONTENT).toString(),
            videoUrl = intent?.getStringExtra(EXTRA_VIDEO_URL).toString()
        ) else null
}