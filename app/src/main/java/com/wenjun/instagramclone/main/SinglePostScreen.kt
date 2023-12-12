package com.wenjun.instagramclone.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.R
import com.wenjun.instagramclone.data.PostData
import com.wenjun.instagramclone.main.CommentsScreen

@Composable
fun SinglePostScreen(navController: NavController, vm: IgViewModel, post: PostData){
    // Text(text = post.postDescription ?: "No description available")
    post.userId?.let{
        Column(modifier = Modifier
            .fillMaxWidth()
            //.wrapContentHeight() // leave a big blank gap here
            .padding(8.dp)
        ){
            Text(text = "Back", modifier = Modifier.clickable { navController.popBackStack()})

            CommonDivider()

            SinglePostDisplay(navController = navController, vm = vm, post = post)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SinglePostDisplay(navController: NavController, vm: IgViewModel, post: PostData){
    val userData = vm.userData.value //get userData from view model
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)){//contains user image and username
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card(shape = CircleShape, modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
            ){
                Image(painter = rememberImagePainter(data = post.userImage), contentDescription = null)
            }
            Text(text = post.username ?: "")
            Text(text = ".", modifier = Modifier.padding(8.dp))

            if(userData?.userId == post.userId) { // if the user is the same with the post user
                // current user's post. Don't show anything
            }else if(userData?.following?.contains(post.userId) == true){ // else if the user has followed current user, display Following
                Text(
                    text = "Following",
                    color = Color.Gray,
                    modifier = Modifier.clickable { vm.onFollowClick(post.userId!!) }
                )
            }else{ //not follow current user yet, give a follow option
                Text(
                    text = "Follow",
                    color = Color.Blue,
                    modifier = Modifier.clickable { vm.onFollowClick(post.userId!!) } //follow a user
                )
            }
        }
    }
    Box {
        val modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 150.dp) // let post image has a minimum height
        CommonImage(data = post.postImage, modifier = modifier, contentScale = ContentScale.FillWidth)
    }
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically){
        Image(
            painter = painterResource(id = R.drawable.ic_like),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color.Red)
        )
        Text(text = " ${post.likes?.size ?: 0} likes", modifier = Modifier.padding(start = 0.dp))
    }
    
    Row(modifier = Modifier.padding(8.dp)){
        Text(text = post.username ?: "", fontWeight = FontWeight.Bold)
        Text(text = post.postDescription ?: "", modifier = Modifier.padding(start = 8.dp))
    }
    
    Row(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "10 comments",
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 8.dp)
                .clickable {
                    post.postId?.let {
                        navController.navigate(DestinationScreen.Comments.createRoute(it)) //create route by postId, here used built-in method navigate
                    }
                }
        )
    }

}