/**
 * Helper UIs and functions
 */
package com.wenjun.instagramclone.main //main package: handle all events in MainActivity

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.R

/**
 *  Error handling UI, pop up error notification.
 */
@Composable
fun NotificationMessage(vm: IgViewModel){
    val notifState = vm.popupNotification.value //get notification state
    val notifMessage = notifState?.getContentOrNull() //handle event
    if(notifMessage != null){ //if there is error handling, display a Toast message
        Toast.makeText(LocalContext.current, notifMessage, Toast.LENGTH_LONG).show()
    }
}

/**
 * show spinner when loading data
 */
@Composable
fun CommonProgressSpinner() {
    Row(
        modifier = Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) { }
            .fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        CircularProgressIndicator()
    }
}

fun navigateTo(navController: NavController, dest: DestinationScreen){
    navController.navigate(dest.route){
        popUpTo(dest.route) //if user already launched the screen previously, pop all latest screen out of stack until desired one
        launchSingleTop = true //ensure only launch the screen one time
    }
}

/**
 * If user signed in already, app will remember at every launch time, so the signed in used will be direct to Feed screen
 */
@Composable
fun CheckSignedIn(navController: NavController, vm: IgViewModel){
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if(signedIn && !alreadyLoggedIn.value){
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route){
            popUpTo(0) //pop all screens to avoid going to login screen
            launchSingleTop = true
        }
    }
}

/**
 * User post image & User icon UI
 * pass param to customize
 */
@Composable
fun CommonImage(
    data: String?, // image Url
    modifier: Modifier = Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop //unify image scale/size: Center crop the image into the available space.
){
    val painter = rememberImagePainter(data = data)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
    if(painter.state is ImagePainter.State.Loading){ // if image is loading, show spinner
        CommonProgressSpinner()
    }
}

/**
 * User image card
 * pass param to customize
 */
@Composable
fun UserImageCard(
    userImage: String?, // nullable, image url
    modifier: Modifier = Modifier // set default modifier
        .padding(8.dp)
        .size(80.dp)
){
    Card(shape = CircleShape, modifier = modifier) {
        if(userImage.isNullOrEmpty()){ // if no user image url, display default icon
            Image(
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }else{ // otherwise, display commonImage
            CommonImage(data = userImage)
        }
    }
}