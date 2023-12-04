package com.wenjun.instagramclone.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wenjun.instagramclone.DestinationScreen
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.R
import com.wenjun.instagramclone.main.CommonProgressSpinner
import com.wenjun.instagramclone.main.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, vm: IgViewModel){
    // the focus variable is an instance of FocusManager obtained from LocalFocusManager.current.
    // the focus variable is used to interact with FocusManager
    val focus = LocalFocusManager.current  // used to close keyboard after clicked login button

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState())
            .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            val emailState = remember{ mutableStateOf(TextFieldValue()) }
            val passState = remember { mutableStateOf(TextFieldValue()) }

            Image(painter = painterResource(
                id = R.drawable.ig_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Login",
                modifier = Modifier.padding(16.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif
            )
            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Email") }
            )
            OutlinedTextField(
                value = passState.value,
                onValueChange = { passState.value = it },
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation() //add visual filter for password
            )
            Button(onClick = { // clear the focus from any focused component when the "LOGIN" button is clicked
                // ensure that the focus is not retained on any specific input field or other interactive element after a button is clicked.
                focus.clearFocus(force = true) //clears the focus from any currently focused component: close keyboard here
            },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "LOGIN")
            }
            Text(text = "New here? Go to signup ->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { // click link to navigate to Signup
                        navigateTo(navController, DestinationScreen.Signup)
                    }
            )
        }
        // show spinner in middle of login page when login is in progress
        val isLoading = vm.inProgress.value
        if(isLoading){
            CommonProgressSpinner()
        }
    }

}