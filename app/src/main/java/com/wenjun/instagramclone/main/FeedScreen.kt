package com.wenjun.instagramclone.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@Composable
fun FeedScreen(navController: NavController, vm: IgViewModel){
    Text(text = "Feed Screen")
}