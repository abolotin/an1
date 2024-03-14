package ru.netology.nmedia.domain

import ru.netology.nmedia.dto.Post

interface PostInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onRemove(post: Post) {}
    fun onEdit(post: Post) {}
    fun onViewVideo(post: Post) {}
    fun onViewPost(post: Post) {}
}