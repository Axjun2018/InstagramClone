package com.wenjun.instagramclone.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.wenjun.instagramclone.IgViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostScreen(navController: NavController, vm: IgViewModel, encodedUri: String){//uri need to be encoded to pass to navigation
    val imageUri by remember { mutableStateOf(encodedUri)}
    var description by rememberSaveable { mutableStateOf("") }
    val scrollSatet = rememberScrollState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .verticalScroll(scrollSatet)
            .fillMaxWidth()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Cancel", modifier = Modifier.clickable{navController.popBackStack()}) // Cancel to go back previous page
            Text(text = "Post", modifier = Modifier.clickable {
                focusManager.clearFocus() // close soft keyboard
                // call the VM
            })
        }

        CommonDivider()

        Image(
            painter = rememberImagePainter(imageUri),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 150.dp),
            contentScale = ContentScale.FillWidth
        )

        Row(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text(text = "Description")},
                singleLine = false,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    textColor = Color.Black
                )
            )
        }
    }

    val inProcess = vm.inProgress.value
    if(inProcess)
        CommonProgressSpinner()
}