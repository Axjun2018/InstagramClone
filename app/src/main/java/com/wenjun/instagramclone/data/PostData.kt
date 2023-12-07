package com.wenjun.instagramclone.data

data class PostData(
    val postId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val userImage: String? = null, //user can change image then update
    val postImage: String? = null,
    val postDescription: String? = null,
    val time: Long? = null
)