package com.wenjun.instagramclone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wenjun.instagramclone.auth.LoginScreen
import com.wenjun.instagramclone.auth.SignupScreen
import com.wenjun.instagramclone.auth.Profilescreen
import com.wenjun.instagramclone.main.FeedScreen
import com.wenjun.instagramclone.main.MyPostsScreen
import com.wenjun.instagramclone.main.NotificationMessage
import com.wenjun.instagramclone.main.SearchScreen
import com.wenjun.instagramclone.ui.theme.InstagramCloneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InstagramCloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

/**
 * sealed class restricts all child classes/objects only can extend DestinationScreen class.
 * Routes collection class
 */
sealed class DestinationScreen(val route: String){
    // add screen routes
    // Signup(Screen) is a singleton obj extends DestinationScreen() that can only have 1 at a time
    object Signup: DestinationScreen("signup")
    object Login: DestinationScreen("login")

    // add bottom nav items routes
    object Feed: DestinationScreen("feed")
    object Search: DestinationScreen("search")
    object MyPosts: DestinationScreen("myposts")
    object Profile: DestinationScreen("profile")

}

@Composable
fun InstagramApp(){
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController()

    NotificationMessage(vm = vm) //Error handling UI

    // nav screen starts from SignupScreen once we starts app
    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route){
        // add all destination routes here: Screens & Nav items
        composable(DestinationScreen.Signup.route){
            SignupScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Feed.route){
            FeedScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Search.route){
            SearchScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.MyPosts.route){
            MyPostsScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Profile.route){
            Profilescreen(navController = navController, vm = vm)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InstagramCloneTheme {
        InstagramApp()
    }
}