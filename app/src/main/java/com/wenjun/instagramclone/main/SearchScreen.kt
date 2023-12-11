/**
 * Firebase doesn't allow to search a string within a string
 * so we have to split post description into individual keywords for search
 */
package com.wenjun.instagramclone.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wenjun.instagramclone.DestinationScreen

@Composable
fun SearchScreen(navController: NavController, vm: IgViewModel){
    // placeholder
//    Column(modifier = Modifier.fillMaxSize()){
//        Column(modifier = Modifier.weight(1f)) {
//            Text(text = "Search screen")
//        }
//        BottomNavigationMenu(
//            selectedItem = BottomNavigationItem.SEARCH,
//            navController = navController
//        )
//    }
    val searchedPostsLoading = vm.searchedPostsProgress.value
    val searchedPosts = vm.searchedPosts.value
    var searchTerm by rememberSaveable { mutableStateOf("") }

    Column{
        SearchBar(
            searchTerm = searchTerm,
            onSearchChange = { searchTerm = it }, // if search value changed, assign current change
            onSearch = { vm.searchPosts(searchTerm) } // perform search action
        )
        PostList(
            isContextLoading = false,
            postsLoading = searchedPostsLoading,
            posts = searchedPosts,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ){ post ->
            // onPostClick: go into the single post
            navigateTo(
                navController = navController,
                DestinationScreen.SinglePost,
                NavParam("post", post)
            )
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.SEARCH,
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchTerm: String, onSearchChange: (String) -> Unit, onSearch: () -> Unit){ // pass search term state
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchTerm,
        onValueChange = onSearchChange,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, CircleShape), // for border style
        shape = CircleShape, //for TextField shape
        keyboardOptions = KeyboardOptions( // offer keyboard styles
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions( // offer keyboard functionality: enable keyboard to search
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        maxLines = 1,
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            textColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        trailingIcon = { // enable search icon to search
            IconButton(onClick = {
                onSearch()
                focusManager.clearFocus()
            }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        }
    )
}