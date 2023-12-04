/**
 * The Event class is designed to represent events in a system.
 * Each instance of Event holds some content (T) and tracks whether it has been handled.
 */
package com.wenjun.instagramclone.data

/**
 * <out T> is a generic type parameter, allowing the class to handle events with different types of content.
 * The out keyword: means T can be used in a read-only manner.
 */
open class Event<out T>(private val content: T){ // handle any data type T's error
    var hasBeenHandled = false
        private set // The 'private set' modifier means that the property can be read publicly but can only be modified within the class.

    fun getContentOrNull(): T?{
        return if(hasBeenHandled){ //if hasBeenHandled is true, return null
            null
        }else{ //else, set hasBeenHandled to true, return content
            hasBeenHandled = true
            content
        }
    }
}