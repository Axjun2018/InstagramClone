/**
 * use to handle secured authentication of signup/sign in by firebase
 */
package com.wenjun.instagramclone

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
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

    // Code inside init{...} block will be executed when an instance of IgViewModel is created
    // It is part of the primary constructor
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
}