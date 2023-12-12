/**
 * Helper/Common UIs and functions
 */
package com.wenjun.instagramclone.main //main package: handle all events in MainActivity

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.animation.core.spring

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

/**
 * Define a NavParam to pass nav parameters
 */
data class NavParam(
    val name: String,  // name of data object to be sent to navigation
    val value: Parcelable // value of data object to be sent to navigation
)

/**
 * vararg: means the params are optional to pass
 */
fun navigateTo(navController: NavController, dest: DestinationScreen, vararg params: NavParam){ // we can pass as many nav params as we want
    //println("previous route: ${navController.previousBackStackEntry?.destination?.route}")
    //println("current route (navigateTo): ${navController.currentBackStackEntry?.destination?.route}") //mypost

    // if there are any nav params in current entry, put them into parcelable to parse
    for(param in params){
        println("params to be added: ${param.value}") // pass
        navController.currentBackStackEntry?.savedStateHandle?.set(param.name, param.value) //("post", post)
        //println(navController.currentBackStackEntry?.arguments?.getParcelable<PostData>(param.name)?.postDescription)
    }

    // this function directs to MainActivity.kt -- NavHost(){composable(){}}
    navController.navigate(dest.route){
        popUpTo(dest.route)//if user already launched the screen previously, pop all latest screen out of stack until desired one
        launchSingleTop = true //ensure only launch the screen one time
    }

    // TEST
//    println("navigate to route (navigateTo): ${navController.currentBackStackEntry?.destination?.route}")
//    val postData = navController
//        .previousBackStackEntry
//        ?.arguments
//        ?.getParcelable<PostData>("post") // this function cannot get data: null
//    println("get params: ${postData?.postDescription}") //null
}

/**
 * If user signed in already, app will remember at every launch time, so the signed in used will be direct to Feed screen
 */
@Composable
fun CheckSignedIn(navController: NavController, vm: IgViewModel){
    val alreadyLoggedIn = remember { mutableStateOf(false) }
    val signedIn = vm.signedIn.value
    if(signedIn && !alreadyLoggedIn.value){ // if user signin at the first time, navigate to FeedScree
        alreadyLoggedIn.value = true
        navController.navigate(DestinationScreen.Feed.route){
            popUpTo(0) //pop all screens to avoid going to login screen
            //launchSingleTop = true
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

@Composable
fun CommonDivider(){
    Divider(
        color = Color.LightGray,
        thickness = 1.dp,
        modifier = Modifier
            .alpha(0.3f)
            .padding(top = 8.dp, bottom = 8.dp)
    )
}

/**
 * 2 status of like icon animation
 */
private enum class LikeIconSize { // switch like icon from small to large size in a duration
    SMALL,
    LARGE
}
@Composable
fun LikeAnimation(like: Boolean = true){ // if like, show like animation; if unlike, remove like animation
    var sizeState by remember { mutableStateOf(LikeIconSize.SMALL) }
    val transition = updateTransition(targetState = sizeState, label = "")
    val size by transition.animateDp( // Creates a Dp animation as a part of the given Transition. This means the states of this animation will be managed by the Transition.
        label = "",
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        }
    ) { state ->
        when(state){
            LikeIconSize.SMALL -> 0.dp
            LikeIconSize.LARGE -> 150.dp
        }
    }

    Image(
        painter = painterResource(id = if(like) R.drawable.ic_like else R.drawable.ic_dislike),
        contentDescription = null,
        modifier = Modifier.size(size = size),
        colorFilter = ColorFilter.tint(if(like) Color.Red else Color.Gray)
    )
    sizeState = LikeIconSize.LARGE
}