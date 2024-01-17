package ru.netology.nmedia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1098,
            viewsCount = 99999
        )

        with(binding) {
            author.text=post.author;
            published.text=post.published
            content.text=post.content
            likesText.text=post.getLikesCountText()
            sharesText.text=post.getSharesCountText()
            viewsText.text=post.getViewsCountText()

            likeIcon.setOnClickListener {
                if (post.likedByMe) post.likesCount-- else post.likesCount++
                post.likedByMe=!post.likedByMe;
                likesText.text=post.getLikesCountText();
                if (post.likedByMe) likeIcon.setImageResource(R.drawable.ic_liked_24) else likeIcon.setImageResource(R.drawable.ic_like_24)
            }

            shareIcon.setOnClickListener {
                post.sharesCount++;
                sharesText.text=post.getSharesCountText()
            }
        }
    }
}