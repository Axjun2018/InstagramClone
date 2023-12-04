package com.wenjun.instagramclone.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.main.navigateTo

@Composable
fun LoginScreen(navController: NavController, vm: IgViewModel){
    Text(text = "New here? Go to signup ->",
        color = Color.Blue,
        modifier = Modifier
            .padding(8.dp)
            .clickable { // click link to navigate to Signup
                navigateTo(navController, DestinationScreen.Signup)
            }
    )
}