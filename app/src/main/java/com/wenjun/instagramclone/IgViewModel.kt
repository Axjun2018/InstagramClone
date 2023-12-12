/**
 * use to handle secured authentication of signup/sign in by firebase
 */
package com.wenjun.instagramclone

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.wenjun.instagramclone.data.Event
import com.wenjun.instagramclone.data.PostData
import com.wenjun.instagramclone.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

const val USERS = "users" //db table name
const val POSTS = "posts"
/**
 * Inject firebase services to ViewModel
 */
@HiltViewModel
class IgViewModel @Inject constructor(
    val auth: FirebaseAuth,     //authentication user: includes firebase auth methods: create/get currentUser ...
    val db: FirebaseFirestore,  //database user: includes firebase query methods: get data from firebase db
    val storage: FirebaseStorage
): ViewModel() {
    /** Save states for different flags */
    val signedIn = mutableStateOf(false) // flags if user signed in or not
    val inProgress = mutableStateOf(false) // flags if db operation is in progress or not
    val userData = mutableStateOf<UserData?>(null)
    val popupNotification = mutableStateOf<Event<String>?>(null) //handle event errors by checking the state of Event class instance

    // variables for retrieve posts
    val refreshPostsProgress = mutableStateOf(false)
    val posts = mutableStateOf<List<PostData>>(listOf())

    // variables for searched post
    val searchedPosts = mutableStateOf<List<PostData>>(listOf())
    val searchedPostsProgress = mutableStateOf(false)

    val postsFeed = mutableStateOf<List<PostData>>(listOf())
    val postsFeedProgress = mutableStateOf(false)

    /**
     * Code inside init{...} block will be executed when an instance of IgViewModel is created
     * It is part of the primary constructor
    */
    init { //if user signed in, get user data
        // auth.signOut() // use to test signup/login/auto login
        val currentUser = auth.currentUser // firebase auth remembers if current user signed in or not
        signedIn.value = currentUser != null
        currentUser?.uid?.let{ uid ->
            getUserData(uid)
        }
    }

    fun onSignup(username: String, email: String, pass: String){
        // if any field is empty, pop error message
        if(username.isEmpty() or email.isEmpty() or pass.isEmpty()){
            handleException(customMessage = "Please fill in all fields")
            return
        }

        inProgress.value = true
        db.collection(USERS).whereEqualTo("username", username).get() //query method: get row data from users where field is username
            .addOnSuccessListener { documents -> //addOnSuccessListener: To be notified when the task succeeds
                if(documents.size() > 0){ //if there is at least 1 username
                    handleException(customMessage = "Username already exists")
                    inProgress.value = false
                }else{ //otherwise, create a new user and sign in
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener{task -> //addOnCompleteListener: To handle success and failure in the same listener
                            if(task.isSuccessful){ // Task completed successfully
                                signedIn.value = true
                                //Create profile
                                createOrUpdateProfile(username = username) // create/update user profile based on signup/signin username
                            }else{ // Task failed with an exception
                                handleException(task.exception, "Signup failed")
                            }
                            inProgress.value = false
                        }
                }
            }
            .addOnFailureListener(){ // addOnFailureListener: Task failed with an exception

            }
    }

    fun onLogin(email: String, pass: String){
        // if any field is empty, pop error message
        if(email.isEmpty() or pass.isEmpty()){
            handleException(customMessage = "Please fill in all fields")
            return
        }

        inProgress.value = true
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(){ task ->
                if(task.isSuccessful){
                    signedIn.value = true
                    inProgress.value = false
                    auth.currentUser?.uid?.let { uid ->
                        // handleException(customMessage = "Login Success") //use to test login
                        getUserData(uid)
                    }
                }else{
                    handleException(task.exception, "Login failed")
                    inProgress.value = false
                }
            }
            .addOnFailureListener(){exc ->
                handleException(exc, "Login failed")
                inProgress.value = false
            }
    }

    /**
     * A private helper method
     * Use to create a new user or update current user
     * store to userData collection
     */
    private fun createOrUpdateProfile( //uid is auto generated, so no need to create
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null
    ){
        val uid = auth.currentUser?.uid //get current user id: exist or null
        val userData = UserData( //use passed info to update, if passed null, get info from current user
            userId = uid,
            name = name ?: userData.value?.name,
            username = username ?: userData.value?.username,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData.value?.following
        )

        // check if user id's existence in database, if it is, update
        uid?.let {
            inProgress.value = true
            db.collection(USERS).document(uid).get()
                .addOnSuccessListener {
                    if(it.exists()){ //update existing user
                        it.reference.update(userData.toMap())
                            .addOnSuccessListener {
                                this.userData.value = userData
                                inProgress.value = false
                            }
                            .addOnFailureListener(){
                                handleException(it, "Cannot update user")
                                inProgress.value = false
                            }
                    }else{ //create new user
                        db.collection(USERS).document(uid).set(userData)
                        getUserData(uid)
                        inProgress.value = false
                    }
            }.addOnFailureListener(){ exc ->
                    handleException(exc, "Cannot create user")
                    inProgress.value = false
            }
        }
    }

    private fun getUserData(uid: String){
        inProgress.value = true
        db.collection(USERS).document(uid).get()
            .addOnSuccessListener {// success to get user
                val user = it.toObject<UserData>() // parse db data to UserData object
                userData.value = user
                inProgress.value = false
                // popupNotification.value = Event("User data retrieved successfully") // test if signup lead user to sign in
                refreshPosts() //get user posts
                getPersonalizedFeed() //update feeds when retrieve user data: display followed posts
            }
            .addOnFailureListener(){ exc -> //fail to get user: handle exception
                handleException(exc, "Connot retrieve user data")
                inProgress.value = false
            }
    }

    fun handleException(exception: Exception? = null, customMessage: String = ""){
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if(customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
    }

    fun updateProfileData(name: String, username: String, bio: String){
        createOrUpdateProfile(name, username, bio)
    }

    /**
     * Private helper
     * for both profile & post images
     */
    private fun uploadImage(uri: Uri, onSuccess: (Uri) -> Unit){
        inProgress.value = true

        // Creates a new StorageReference initialized at the root Firebase Storage location.
        val storageRef = storage.reference // firebase storage is used to save assets, eg: image assets
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid") // create a child location of current reference
        val uploadTask = imageRef.putFile(uri) // Asynchronously uploads from a content URI to this StorageReference.

        uploadTask
            .addOnSuccessListener {// if uploadTask success,
                val result = it.metadata?.reference?.downloadUrl
                result?.addOnSuccessListener(onSuccess)
            }
            .addOnFailureListener(){exc ->
                handleException(exc)
                inProgress.value = false
            }
    }

    fun uploadProfileImage(uri: Uri){
        uploadImage(uri){
            createOrUpdateProfile(imageUrl = it.toString())
            updatePostUserImageData(it.toString()) // after profile image was updated, the user image in single post should also be updated
        }
    }

    /**
     * Bc firebase is NOSQL but not relational db, so we need extra step to update user icon in single post screen
     */
    private fun updatePostUserImageData(imageUrl: String){
        val currentUid = auth.currentUser?.uid
        db.collection(POSTS).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener {
                val posts = mutableStateOf<List<PostData>>(arrayListOf())
                convertPosts(it, posts)
                val refs = arrayListOf<DocumentReference>()
                for (post in posts.value){
                    post.postId?.let{ id ->
                        refs.add(db.collection(POSTS).document(id))
                    }
                }
                if (refs.isNotEmpty()){
                    db.runBatch{ batch ->
                        for(ref in refs){
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }
                        .addOnSuccessListener {
                            refreshPosts()
                        }
                }
            }
    }

    fun onLogout(){
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Logged out")
        searchedPosts.value = listOf() // when logout, clear all searched posts
        postsFeed.value = listOf() //  when logout, clear all feeds
    }

    fun onNewPost(uri: Uri, description: String, onPostSuccess: () -> Unit){
        uploadImage(uri){
            onCreatePost(it, description, onPostSuccess)
        }
    }

    /**
     * Create post and upload to fire store
     */
    private fun onCreatePost(imageUrl: Uri, description: String, onPostSuccess: () -> Unit){
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.username
        val currentUserImage = userData.value?.imageUrl

        if(currentUid != null){ //if the uid exists
            // generate a new UUID for post
            val postUuid = UUID.randomUUID().toString()

            // exclude filler words
            val fillerWords = listOf("the", "be", "to", "am", "is", "was", "of", "and", "or", "a", "an", "in", "it", "I'm", "i'm" )
            // extract keywords
            val searchTerms = description
                .split(" ", ".", ",", "?", "!", "#", ":") // split post description by given symbol
                .map { it.lowercase() } // map all keywords to lowercase
                .filter { it.isNotEmpty() and !fillerWords.contains(it) } // filter the empty keywords or the words in fillerWords

            // create PostData instance
            val post = PostData(
                postId = postUuid,
                userId = currentUid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = imageUrl.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf<String>(),
                searchTerms = searchTerms //assign keywords to search terms
            )

            // save into database
            db.collection(POSTS).document(postUuid).set(post)
                .addOnSuccessListener {//if save successfully
                    popupNotification.value = Event("Post successfully created.")
                    inProgress.value = false
                    refreshPosts()    //after new post was added, refresh to display
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Unable to create post")
                    inProgress.value = false
                }
        }else{ // else fail to save
            handleException(customMessage = "Error: username unavailable. Unable to create post.")
            onLogout()
            inProgress.value = false
        }
    }

    private fun refreshPosts(){
        val currentUid = auth.currentUser?.uid
        if(currentUid != null){
            refreshPostsProgress.value = true
            db.collection(POSTS).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    convertPosts(documents, posts)
                    refreshPostsProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "Cannot fetch posts")
                    refreshPostsProgress.value = false
                }
        }else{
            handleException(customMessage = "Error: username is unavailable. Unable to refresh posts")
            onLogout()
        }
    }

    /**
     * retrieve posts from db, convert them to mutableStateOf(List<PostData>) structure
     */
    private fun convertPosts(documents: QuerySnapshot, outState: MutableState<List<PostData>>){
        val newPosts = mutableListOf<PostData>()
        documents.forEach { doc ->
            val post = doc.toObject<PostData>()
            newPosts.add(post)
        }
        val sortedPosts = newPosts.sortedByDescending {it.time}
        outState.value = sortedPosts // assign sorted post to result
    }

    /**
     * Search screen functionality
     */
    fun searchPosts(searchTerm: String){
        if(searchTerm.isNotEmpty()){
            searchedPostsProgress.value = true
            db.collection(POSTS) //field name: same with db, trim whitespace, convert to lowercase
                .whereArrayContains("searchTerms", searchTerm.trim().lowercase())
                .get()// get posts data by search terms
                .addOnSuccessListener {//if get successful
                    convertPosts(it, searchedPosts) // convert post to mutable list data
                    searchedPostsProgress.value = false // finish progress
                } // if failed, handle exception
                .addOnFailureListener {exc ->
                    handleException(exc, "Cannot search posts")
                    searchedPostsProgress.value = false
                }
        }
    }

    /**
     * Update followers for current user, and update userData
     */
    fun onFollowClick(userId: String){
        auth.currentUser?.uid?.let{ currentUser ->  // if there is the current user
            val following = arrayListOf<String>() // get user's following list
            userData.value?.following?.let {//if following has value, add all to following
                following.addAll(it)
            }
            if(following.contains(userId)){ // if post user has been followed
                following.remove(userId)// click to unfollow
            }else{
                following.add(userId)// otherwise, follow post user
            }
            db.collection(USERS).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)
                }
        }
    }

    private fun getPersonalizedFeed(){
        val following = userData.value?.following
        if(!following.isNullOrEmpty()){
            postsFeedProgress.value = true
            db.collection(POSTS).whereIn("userId", following).get() //get userId field in following list
                .addOnSuccessListener {
                    convertPosts(documents = it, outState = postsFeed)
                    if(postsFeed.value.isEmpty()){ // if user has no following: no feeds, get general feed
                        getGeneralFeed()
                    }else{
                        postsFeedProgress.value = false
                    }
                }
                .addOnFailureListener{exc ->
                    handleException(exc, "Cannot get personalized feed")
                    postsFeedProgress.value = false
                }
        }else{
            getGeneralFeed()
        }
    }

    /**
     * if use has no followers, return general feeds posted in last day
     */
    private fun getGeneralFeed(){
        postsFeedProgress.value = true
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 60 * 60 * 1000 // 1 day in millis
        db.collection(POSTS).whereGreaterThan("time", currentTime - difference)
            .get()
            .addOnSuccessListener {
                convertPosts(documents = it, outState = postsFeed)
                postsFeedProgress.value = false
            }
            .addOnFailureListener{exc ->
                handleException(exc, "Connot get feed")
                postsFeedProgress.value = false
            }
    }

    fun onLikePost(postData: PostData) { //PostData contains likes
        auth.currentUser?.uid?.let { userId -> //if current user exists
            postData.likes?.let { likes -> // if post has likes
                val newLikes = arrayListOf<String>() // create newLikes list
                if(likes.contains(userId)){ // if the likes list contains current user itself
                    newLikes.addAll(likes.filter{ userId != it }) // add all userId that's not current user to likes
                }else{ // otherwise, add all userId who liked to newLikes
                    newLikes.addAll(likes)
                    newLikes.add(userId)
                }
                postData.postId?.let {  postId ->
                    db.collection(POSTS).document(postId).update("likes", newLikes) //update likes field with newLikes data
                        .addOnSuccessListener {
                            postData.likes = newLikes
                        }
                        .addOnFailureListener { exc ->
                            handleException(exc, "Unable to like post")
                        }
                }
            }

        }
    }

}
