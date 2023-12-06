/**
 * Bottom Nav UI
 */
package com.wenjun.instagramclone.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.R

// define nav items in enum class
enum class BottomNavigationItem(val icon: Int, val navDestination: DestinationScreen){
    // set ico and route for each bottom menu
    FEED(R.drawable.ic_home, DestinationScreen.Feed),
    SEARCH(R.drawable.ic_search, DestinationScreen.Search),
    POSTS(R.drawable.ic_posts, DestinationScreen.MyPosts)
}

@Composable
fun BottomNavigationMenu(selectedItem: BottomNavigationItem, navController: NavController){
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 4.dp)
        .background(Color.White)
    ){// iterate nav item indices to display each nav item UI
        for(item in BottomNavigationItem.values()){
            Image(
                painter = painterResource(id = item.icon), //item icon is from vector asset
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp)
                    .weight(1f) //Size the element's width proportional to its weight relative to other weighted sibling elements in the Row.
                    .clickable {
                        navigateTo(navController, item.navDestination)
                    },
                colorFilter = if(item == selectedItem) ColorFilter.tint(Color.Black) //if nav menu item is selected: fill black
                    else ColorFilter.tint(Color.Gray) //else: fill gray
                )
        }
    }
}