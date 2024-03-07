package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var nextId = 0L;
    private var posts = listOf(
        Post(
            id = ++nextId,
            author = "Post 1: Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            videoUrl = "https://www.youtube.com/watch?v=WhWc3b3KhnY",
            published = "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1_099_999,
            viewsCount = 99999
        ),
        Post(
            id = ++nextId,
            author = "Post 2: Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1_099_999,
            viewsCount = 99999
        ),
        Post(
            id = ++nextId,
            author = "Post 3: Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1_099_999,
            viewsCount = 99999
        ),
        Post(
            id = ++nextId,
            author = "Post 4: Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1_099_999,
            viewsCount = 99999
        ),
        Post(
            id = ++nextId,
            author = "Post 5: Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "21 мая в 18:36",
            likesCount = 999,
            sharesCount = 1_099_999,
            viewsCount = 99999
        )
    )
    private val data = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id == id)
                it.copy(
                    likesCount = if (it.likedByMe) it.likesCount - 1 else it.likesCount + 1,
                    likedByMe = !it.likedByMe
                )
            else it
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id == id) {
                it.copy(sharesCount = it.sharesCount + 1)
            } else it
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts;
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            // Новый пост
            posts = listOf(
                post.copy(
                    id = ++nextId,
                    author = "Me",
                    content = post.content,
                    videoUrl = post.videoUrl
                )
            ) + posts
        } else {
            // Измененный пост
            posts = posts.map {
                if (it.id == post.id) it.copy(
                    content = post.content,
                    videoUrl = post.videoUrl
                ) else it
            }
        }
        data.value = posts
    }
}