package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        1,
        "Нетология. Университет интернет-профессий будущего",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        "21 мая в 18:36",
        likesCount = 999,
        sharesCount = 1_099_999,
        viewsCount = 99999
    )

    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        post = post.copy(
            likesCount = if (post.likedByMe) post.likesCount - 1 else post.likesCount + 1,
            likedByMe = !post.likedByMe
        )
        data.value = post
    }

    override fun share() {
        post = post.copy(
            sharesCount = post.sharesCount + 1
        )
        data.value = post
    }
}