package com.wenjun.instagramclone.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsScreen(navController: NavController, vm: IgViewModel, postId: String){
//    Text(text = "Comments Screen")
    var commentText by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier.weight(1f)){
            Text(text = "Comments")
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        ){
            TextField(
                value = commentText,
                onValueChange = {commentText = it},
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color.LightGray),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )
            Button(onClick = {
                vm.createComment(postId = postId, text = commentText)
                commentText = ""
                focusManager.clearFocus()
                },
                modifier = Modifier.padding(start = 8.dp)
            ){
                Text(text = "Comment")
            }
        }
    }

}