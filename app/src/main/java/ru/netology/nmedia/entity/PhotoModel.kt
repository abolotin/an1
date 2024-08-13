package ru.netology.nmedia.entity

import android.net.Uri
import ru.netology.nmedia.dto.Post
import java.io.File

data class PhotoModel(
    val uri: Uri? = null,
    val file: File? = null
) {
    val isEmpty: Boolean
        get() = (uri == null)
}