package com.wenjun.instagramclone.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.R
import com.wenjun.instagramclone.data.PostData

/**
 * A PostRow contains 3 posts which will be place in a lazy column: PostList()
 */
data class PostRow(
    var post1: PostData? = null,
    var post2: PostData? = null,
    var post3: PostData? = null
){
    fun isFull() = post1 != null && post2 != null && post3 != null
    fun add(post: PostData) {
        if(post1 == null){
            post1 = post
        }else if(post2 == null){
            post2 = post
        }else if(post3 == null){
            post3 = post
        }
    }
}
@Composable
fun MyPostsScreen(navController: NavController, vm: IgViewModel){
    // retrieve images from device and pass selected image to MyPostsScreen to display
    val newPostImageLauncher = rememberLauncherForActivityResult( // registry a device activity, returns the launcher that can be used to start the activity
        contract = ActivityResultContracts.GetContent(), //contract with standard Android activity: get content from device, in this case
    ){uri -> //onResult = {} - the callback to be called on the main thread when activity result is available
        uri?.let{
            val encoded = Uri.encode(it.toString())
            val route = DestinationScreen.NewPost.createRoute(encoded)
            navController.navigate(route)
        }
    }

    val userData = vm.userData.value
    val isLoading = vm.inProgress.value
    val postsLoading = vm.refreshPostsProgress.value // check if posts are refreshing
    val posts = vm.posts.value //get current posts from mutableStateOf<List<PostData>>
    
    Column {
        Column(modifier = Modifier.weight(1f)){
            Row {
                ProfileImage(userData?.imageUrl){ // define trilling lambda in this block
                    newPostImageLauncher.launch("image/*") // *: means for all image types
                }
                Text(
                    text = "15\nposts",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "54\nfollowers",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "0\nfollowing",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center
                )
            }
            // display username, name, bio, if the database has the userData
            Column(modifier = Modifier.padding(8.dp)){
                val usernameDisplay = if(userData?.username == null) "" else "@${userData?.username}"
                Text(text = userData?.name ?: "", fontWeight = FontWeight.Bold)
                Text(text = usernameDisplay)
                Text(text = userData?.bio ?: "") //means if bio is null, return ""
            }
            OutlinedButton(
                onClick = { navigateTo(navController, DestinationScreen.Profile) }, // click to go to ProfileScreen
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.elevatedButtonElevation( // shadow effect below button
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    disabledElevation = 0.dp
                ),
                shape = RoundedCornerShape(10)
            ){
                Text(text = "Edit Profile", color = Color.Black)
            }
//            Column(modifier = Modifier.weight(1f)){
//                Text(text = "Posts list")
//            }
            PostList(
                isContextLoading = isLoading,
                postsLoading = postsLoading,
                posts = posts,
                modifier = Modifier.weight(1f).padding(1.dp).fillMaxSize()
            ){ post -> // each post navigate to different page
                //onPostClick()
                // println("MyPost: postDescription = " + post.postDescription) //postDescription showed
                navigateTo(
                    navController = navController, // stack top: mypost
                    DestinationScreen.SinglePost,  // going to add: singlepost
                    NavParam("post", post) // construct a NavParam, "post" as key name, post is a PostData value
                )
            }
        }
        BottomNavigationMenu(
            selectedItem = BottomNavigationItem.POSTS,
            navController = navController
        )
    }
    if(isLoading){
        CommonProgressSpinner()
    }
}

/**
 * Profile image for user
 */
@Composable
fun ProfileImage(imageUrl: String?, onClick: () -> Unit){
    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable { onClick.invoke() } //onCustomEvent.invoke(): when click profile icon, trigger the passed event function
    ){
        // user profile icon
        UserImageCard(
            userImage = imageUrl,
            modifier = Modifier
                .padding(8.dp)
                .size(64.dp)
        )
        // + icon: to change user icon
        Card(
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, color = Color.White),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 8.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier
                    .background(Color(0xFF495d92))
            )
        }
    }
}

/**
 * UI displays all posts
 */
@Composable
fun PostList(
    isContextLoading: Boolean,
    postsLoading: Boolean,
    posts: List<PostData>,
    modifier: Modifier,
    onPostClick: (PostData) -> Unit
) {
    if(postsLoading){
        CommonProgressSpinner()
    }else if(posts.isEmpty()){
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            if(!isContextLoading) Text(text = "No posts available")
        }
    }else{
        LazyColumn(modifier = modifier){
            val rows = arrayListOf<PostRow>()
            var currentRow = PostRow()
            rows.add(currentRow)
            for(post in posts){
                if(currentRow.isFull()){ // if currentRow is full
                    currentRow = PostRow() // create a new PostRow
                    rows.add(currentRow)  // add the new PostRow to rows
                }
                currentRow.add(post = post) // current row add post
            }
            items(items = rows){row ->
                PostsRow(item = row, onPostClick = onPostClick)
            }
        }
    }
}

/**
 * UI for one post row: contains 3 clickable image posts
 */
@Composable
fun PostsRow(item: PostRow, onPostClick: (PostData) -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(120.dp)){
        PostImage(imageUrl = item.post1?.postImage, modifier = Modifier
            .weight(1f)
            .clickable { item.post1?.let { post -> onPostClick(post) } }
        )
        PostImage(imageUrl = item.post2?.postImage, modifier = Modifier
            .weight(1f)
            .clickable { item.post2?.let { post -> onPostClick(post) } }
        )
        PostImage(imageUrl = item.post3?.postImage, modifier = Modifier
            .weight(1f)
            .clickable { item.post3?.let { post -> onPostClick(post) } }
        )
    }
}

/**
 * UI for 1 post: display a clickable image
 */
@Composable
fun PostImage(imageUrl: String?, modifier: Modifier){
    Box(modifier = modifier){
        var modifier = Modifier
            .padding(1.dp)
            .fillMaxSize()
        if(imageUrl == null){ //if there is no post2 or post3, then set it unclickable
            modifier = modifier.clickable(enabled = false) {  }
        }
        CommonImage(data = imageUrl, modifier = modifier, contentScale = ContentScale.Crop)
    }
}