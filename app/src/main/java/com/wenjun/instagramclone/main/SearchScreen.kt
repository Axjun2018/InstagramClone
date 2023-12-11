/**
 * Firebase doesn't allow to search a string within a string
 * so we have to split post description into individual keywords for search
 */
package com.wenjun.instagramclone.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@Composable
fun SearchScreen(navController: NavController, vm: IgViewModel){
    Column(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Search screen")
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.SEARCH,
            navController = navController
        )
    }
}