package com.wenjun.instagramclone.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@Composable
fun FeedScreen(navController: NavController, vm: IgViewModel){
    // Text(text = "Feed Screen")
    Column(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Feed screen")
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.FEED,
            navController = navController
        )
    }
}