/**
 * use to handle secured authentication of signup/sign in by firebase
 */
package com.wenjun.instagramclone

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wenjun.instagramclone.data.Event
import com.wenjun.instagramclone.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

const val USERS = "users" //db table name
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

    fun onSignup(username: String, email: String, pass: String){
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

    private fun createOrUpdateProfile( //uid is auto generated, so no need to create
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null
    ){
        val uid = auth.currentUser?.uid //get current user id: exist or null
        val userData = UserData( //use passed info update, if passed null, get info from current user
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

    }

    fun handleException(exception: Exception? = null, customMessage: String = ""){
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if(customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)
    }
}