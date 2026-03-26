package com.atu.jira.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.components.MainButton
import com.atu.jira.utils.ResourceState
import com.atu.jira.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel { AuthViewModel() },
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is ResourceState.Loading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is ResourceState.Loading
            )

            Spacer(Modifier.height(24.dp))

            MainButton(
                text = if (authState is ResourceState.Loading) "Logging in..." else "Login",
                enabled = authState !is ResourceState.Loading,
                onClick = {
                    viewModel.loginUser(email, password, onLoginSuccess)
                }
            )

            Spacer(Modifier.height(12.dp))
            Text(
                "Don't have an account? Signup",
                modifier = Modifier.clickable { onSignupClick() },
                color = MaterialTheme.colorScheme.primary
            )

            if (authState is ResourceState.Error) {
                Spacer(Modifier.height(16.dp))
                Text((authState as ResourceState.Error).message, color = Color.Red)
            }
        }
        
        if (authState is ResourceState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}

@Composable
fun SignupScreen(
    viewModel: AuthViewModel = viewModel { AuthViewModel() },
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Signup", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is ResourceState.Loading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is ResourceState.Loading
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = authState !is ResourceState.Loading
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.signupUser(email, name, password, onSignupSuccess, onLoginClick)
                },
                enabled = authState !is ResourceState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (authState is ResourceState.Loading) "Creating..." else "Signup")
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "Already have an account? Login",
                modifier = Modifier.clickable { onLoginClick() },
                color = MaterialTheme.colorScheme.primary
            )

            if (authState is ResourceState.Error) {
                Spacer(Modifier.height(16.dp))
                Text((authState as ResourceState.Error).message, color = Color.Red)
            }
        }

        if (authState is ResourceState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
        }
    }
}
