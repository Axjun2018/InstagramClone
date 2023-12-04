/**
 * Error handling UI, pop up error notification.
 */
package com.wenjun.instagramclone.main //main package: handle all events in MainActivity

import android.widget.Toast
import androidx.compose.runtime.Composable
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