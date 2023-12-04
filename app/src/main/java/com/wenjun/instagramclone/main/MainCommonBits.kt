/**
 * Error handling UI, pop up error notification.
 */
package com.wenjun.instagramclone.main //main package: handle all events in MainActivity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.wenjun.instagramclone.IgViewModel

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