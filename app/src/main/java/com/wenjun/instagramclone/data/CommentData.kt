package com.wenjun.instagramclone.data

import android.adservices.adid.AdId
import java.sql.Timestamp

data class CommentData(
    val commentId: String? = null,
    val postId: String? = null,
    val username: String? = null,
    val text: String? = null,
    val timestamp: Long? = null
)
