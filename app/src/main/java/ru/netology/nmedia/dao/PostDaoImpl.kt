package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val ddls = """
            CREATE TABLE ${PostColumns.TABLE} (
                ${PostColumns.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PostColumns.COL_AUTHOR} TEXT NOT NULL,
                ${PostColumns.COL_CONTENT} TEXT NOT NULL DEFAULT '',
                ${PostColumns.COL_VIDEO_URL} TEXT NOT NULL DEFAULT '',
                ${PostColumns.COL_PUBLISHED} TEXT NOT NULL,
                ${PostColumns.COL_LIKE_BY_ME} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COL_LIKES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COL_SHARES} INTEGER NOT NULL DEFAULT 0,
                ${PostColumns.COL_VIEWS} INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
    }

    object PostColumns {
        const val TABLE = "posts"
        const val COL_ID = "id"
        const val COL_AUTHOR = "author"
        const val COL_CONTENT = "content"
        const val COL_VIDEO_URL = "videoUrl"
        const val COL_PUBLISHED = "published"
        const val COL_LIKE_BY_ME = "likeByMe"
        const val COL_LIKES = "likes"
        const val COL_SHARES = "shares"
        const val COL_VIEWS = "views"
        val ALL_COLUMNS = arrayOf(
            COL_ID,
            COL_AUTHOR,
            COL_CONTENT,
            COL_VIDEO_URL,
            COL_PUBLISHED,
            COL_LIKE_BY_ME,
            COL_LIKES,
            COL_SHARES,
            COL_VIEWS
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COL_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(map(it))
            }
        }

        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COL_ID, post.id)
            }

            put(PostColumns.COL_AUTHOR, "Me")
            put(PostColumns.COL_PUBLISHED, "now")
            put(PostColumns.COL_CONTENT, post.content)
            put(PostColumns.COL_VIDEO_URL, post.videoUrl)
        }

        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COL_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun likeById(id: Long) {
        db.execSQL(
            """
                UPDATE ${PostColumns.TABLE} SET
                    ${PostColumns.COL_LIKES} = ${PostColumns.COL_LIKES} + CASE WHEN ${PostColumns.COL_LIKE_BY_ME} THEN -1 ELSE 1 END,
                    ${PostColumns.COL_LIKE_BY_ME} = CASE WHEN ${PostColumns.COL_LIKE_BY_ME} THEN 0 ELSE 1 END
                WHERE ${PostColumns.COL_ID} = ?
            """.trimIndent(),
            arrayOf(id.toString())
        )
    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COL_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COL_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COL_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COL_CONTENT)),
                videoUrl = getString(getColumnIndexOrThrow(PostColumns.COL_VIDEO_URL)),
                published = getString(getColumnIndexOrThrow(PostColumns.COL_PUBLISHED)),
                likedByMe = getInt(getColumnIndexOrThrow(PostColumns.COL_LIKE_BY_ME)) != 0,
                likesCount = getLong(getColumnIndexOrThrow(PostColumns.COL_LIKES)),
                sharesCount = getLong(getColumnIndexOrThrow(PostColumns.COL_SHARES)),
                viewsCount = getLong(getColumnIndexOrThrow(PostColumns.COL_VIEWS))
            )
        }
    }
}