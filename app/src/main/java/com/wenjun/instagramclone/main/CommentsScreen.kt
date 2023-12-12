package com.wenjun.instagramclone.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@Composable
fun CommentsScreen(navController: NavController, vm: IgViewModel, postId: String){
    Text(text = "Comments Screen")
}