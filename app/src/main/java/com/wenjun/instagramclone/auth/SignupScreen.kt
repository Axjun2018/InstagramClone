package com.wenjun.instagramclone.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.wenjun.instagramclone.IgViewModel
import com.wenjun.instagramclone.R
import com.wenjun.instagramclone.main.CommonProgressSpinner

/**
 * SignupScreen composable passes nav status and view model
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navController: NavController, vm: IgViewModel){
    //Text(text = "SignupScreen") //test
    Box(modifier = Modifier.fillMaxWidth()){
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState()) // for scrollable UI, must remember the scroll state
            .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** remember the state of string values in TextField */
            val usernameState = remember { mutableStateOf(TextFieldValue())}
            val emailState = remember { mutableStateOf(TextFieldValue())}
            val passState = remember { mutableStateOf(TextFieldValue())}

            Image(
                painter = painterResource(R.drawable.ig_logo),
                contentDescription = null,
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 16.dp)
                    .padding(8.dp)
            )
            Text(
                text = "Signup",
                modifier = Modifier.padding(8.dp),
                fontSize = 30.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = usernameState.value, //set input value to state value
                onValueChange = { usernameState.value = it }, //if input value change, update state value; it: means current input value
                modifier = Modifier.padding(8.dp),
                label = { Text(text = "Username") }
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
            Button(onClick = {
                             vm.onSignup(
                                 usernameState.value.text,
                                 emailState.value.text,
                                 passState.value.text
                             )
            },
                modifier = Modifier.padding()
            ) {
                Text(text = "SIGN UP")
            }
            Text(text = "Already a user? Go to login ->",
                color = Color.Blue,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { }
                )
        }

        // show spinner in middle of signup page when loading data
        val isLoading = vm.inProgress.value
        if(isLoading){
            CommonProgressSpinner()
        }
    }
}