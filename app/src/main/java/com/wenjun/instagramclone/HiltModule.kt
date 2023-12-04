package com.wenjun.instagramclone

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * Declare firebase service functions as needed
 */
@Module
@InstallIn(ViewModelComponent::class) //provides following firebase services to ViewModelComponent type
class HiltModule {
    @Provides
    fun provideAuthentication(): FirebaseAuth = Firebase.auth

    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideStorage(): FirebaseStorage = Firebase.storage
}