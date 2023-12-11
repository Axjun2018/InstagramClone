package com.wenjun.instagramclone.data

import android.os.Parcel
import android.os.Parcelable

data class PostData(
    val postId: String? = null,
    val userId: String? = null,
    val username: String? = null,
    val userImage: String? = null, //user can change image then update
    val postImage: String? = null,
    val postDescription: String? = null,
    val time: Long? = null,
    val likes: List<String>? = null // a list of userId that liked the post
): Parcelable {
    /** Interface for classes whose instances can be written to and restored from a Parcel:
     * We need to pass PostData through navigation, so PostData should be parsed.
     * We can add/remove class params along with the first 2 methods.
     * Chose Parcelable implementation to add all code below:
     */
    // Step2: construct PostData obj when navigate to SinglePostScreen by reading these parcelables
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.createStringArrayList()
    ) {
    }
    // step1: pass all params to this fun
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(userId)
        parcel.writeString(username)
        parcel.writeString(userImage)
        parcel.writeString(postImage)
        parcel.writeString(postDescription)
        parcel.writeValue(time)
        parcel.writeStringList(likes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PostData> {
        override fun createFromParcel(parcel: Parcel): PostData {
            return PostData(parcel)
        }

        override fun newArray(size: Int): Array<PostData?> {
            return arrayOfNulls(size)
        }
    }
}
