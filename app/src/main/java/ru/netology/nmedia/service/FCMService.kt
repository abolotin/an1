package ru.netology.nmedia.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

enum class Action {
    LIKE,
    NEWPOST
}

data class PushMessage(
    val recipientId: Long?,
    val content: String?
)

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String
)

data class NewPost(
    val author: String,
    val authorId: Long,
    val content: String,
    val videoUrl: String,
    val published: String
)

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val recipientId = "recipientId"
    private val content = "content"
    private val gson = Gson()
    private val channelId = "nmediaChannel"
    
    @Inject
    lateinit var appAuth: AppAuth

    // private val repository: PostRepository = PostRepositoryRetrofitImpl()


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_title)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                channelId,
                name,
                importance
            ).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        try {
            val pushMessage = gson.fromJson(message.data[content], PushMessage::class.java)
            pushMessage.recipientId?.let {
                if ((!appAuth.isAuthorized)
                    || (appAuth.state.value?.id != it)) {
                    appAuth.sendPushToken()
                    return
                }
            }

            showNotification(
                getString(R.string.push_message_title),
                pushMessage.content
            )

            /*
            message.data[action]?.let {
                when (Action.valueOf(it)) {
                    Action.LIKE -> handleLike(
                        gson.fromJson(
                            message.data[content],
                            Like::class.java
                        )
                    )

                    Action.NEWPOST -> handleNewPost(
                        gson.fromJson(
                            message.data[content],
                            NewPost::class.java
                        )
                    )
                }
            }
            */
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        appAuth.sendPushToken(token)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(
        title: String? = null,
        message: String? = null,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification =
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            title?.let {
                notification.setContentTitle(it)
            }

            message?.let {
                notification.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            }

            NotificationManagerCompat.from(this)
                .notify(Random.nextInt(100_000), notification.build())
        }
    }

    private fun handleLike(content: Like) {
        //repository.likeById(content.postId)
        showNotification(
            getString(R.string.notification_liked_post_title),
            getString(
                R.string.notification_liked_post_message,
                content.userName,
                content.postAuthor
            )
        )
    }

    private fun handleNewPost(content: NewPost) {
        val post = Post(
            id = 0,
            author = content.author,
            content = content.content,
            videoUrl = content.videoUrl,
            published = Date(content.published).time,
            authorId = content.authorId
        )
        // repository.saveAsync(post, object: PostRepository.GetPostCallback)
        showNotification(
            getString(
                R.string.notification_new_post_title,
                post.author
            ),
            content.content
        )
    }
}