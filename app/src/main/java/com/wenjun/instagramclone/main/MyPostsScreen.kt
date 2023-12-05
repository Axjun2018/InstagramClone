package com.wenjun.instagramclone.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@Composable
fun MyPostsScreen(navController: NavController, vm: IgViewModel){
    Column(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "MyPosts screen")
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.POSTS,
            navController = navController
        )
    }
}