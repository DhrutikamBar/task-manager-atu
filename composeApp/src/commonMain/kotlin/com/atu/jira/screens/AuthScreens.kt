package com.atu.jira.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atu.jira.components.CenterWrapper
import com.atu.jira.components.CenteredContainer
import com.atu.jira.components.DevicePosture
import com.atu.jira.components.JiraButton
import com.atu.jira.components.JiraTextField
import com.atu.jira.components.MainButton
import com.atu.jira.components.calculateDevicePosture
import com.atu.jira.utils.ResourceState
import com.atu.jira.viewmodel.AuthViewModel
import com.atu.jira.viewmodel.TicketViewModel
import jiraatu.composeapp.generated.resources.Res
import jiraatu.composeapp.generated.resources.ic_atu
import org.jetbrains.compose.resources.painterResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit
) {
    val viewModel: AuthViewModel = remember {
        getKoin().get<AuthViewModel>()
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var isTabletOrDesktop = true
    val posture = calculateDevicePosture()

    when (posture) {
        DevicePosture.Desktop -> {
            isTabletOrDesktop = true
        }

        DevicePosture.Tablet -> {
            isTabletOrDesktop = true
        }

        DevicePosture.Mobile -> {
            isTabletOrDesktop = false
        }
    }

    CenterWrapper {
        Box(modifier = Modifier.size(500.dp)) {

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Login", style = MaterialTheme.typography.headlineMedium)

               /* Image(
                    painter = painterResource(Res.drawable.ic_atu),
                    contentDescription = "Logo",
                )*/
                Spacer(Modifier.height(16.dp))

                JiraTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is ResourceState.Loading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true
                )

                Spacer(Modifier.height(5.dp))
                emailError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(12.dp))


                JiraTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is ResourceState.Loading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true
                )
                Spacer(Modifier.height(5.dp))
                passwordError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(24.dp))


                JiraButton(
                    text = if (authState is ResourceState.Loading) "Logging in..." else "Login",
                    enabled = authState !is ResourceState.Loading,
                    onClick = {
                        var isValid = true

                        if (!isValidEmail(email)) {
                            emailError = "Enter valid email"
                            isValid = false
                        } else emailError = null

                        if (password.isBlank()) {
                            passwordError = "Password cannot be empty"
                            isValid = false
                        } else passwordError = null

                        if (isValid) {
                            viewModel.loginUser(email, password, onLoginSuccess)
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))
                Text(
                    "Don't have an account? Signup",
                    modifier = Modifier
                        .clickable { onSignupClick() }
                        .pointerHoverIcon(PointerIcon.Hand),
                    color = MaterialTheme.colorScheme.primary
                )

                if (authState is ResourceState.Error) {
                    Spacer(Modifier.height(16.dp))
                    Text((authState as ResourceState.Error).message, color = Color.Red)
                }
            }



            if (authState is ResourceState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                )
            }
        }
    }


}

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val viewModel: AuthViewModel = remember {
        getKoin().get<AuthViewModel>()
    }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }


    CenterWrapper {
        Box(modifier = Modifier.size(500.dp)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Signup", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))

                JiraTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is ResourceState.Loading, singleLine = true
                )
                Spacer(Modifier.height(5.dp))
                nameError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(12.dp))


                JiraTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is ResourceState.Loading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true
                )
                Spacer(Modifier.height(5.dp))
                emailError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(12.dp))


                JiraTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    modifier = Modifier.fillMaxWidth(),
                    enabled = authState !is ResourceState.Loading,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true
                )
                Spacer(Modifier.height(5.dp))
                passwordError?.let {
                    Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(24.dp))


                JiraButton(
                    text = if (authState is ResourceState.Loading) "Creating..." else "Signup",
                    enabled = authState !is ResourceState.Loading,
                    onClick = {
                        var isValid = true

                        if (name.isBlank()) {
                            nameError = "Name required"
                            isValid = false
                        } else nameError = null

                        if (!isValidEmail(email)) {
                            emailError = "Enter valid email"
                            isValid = false
                        } else emailError = null

                        if (!isValidPassword(password)) {
                            passwordError = "Min 6 characters required"
                            isValid = false
                        } else passwordError = null

                        if (isValid) {
                            viewModel.signupUser(email, name, password, onSignupSuccess, onLoginClick)
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))
                Text(
                    "Already have an account? Login",
                    modifier = Modifier
                        .clickable { onLoginClick() }
                        .pointerHoverIcon(PointerIcon.Hand),
                    color = MaterialTheme.colorScheme.primary
                )

                if (authState is ResourceState.Error) {
                    Spacer(Modifier.height(16.dp))
                    Text((authState as ResourceState.Error).message, color = Color.Red)
                }
            }

            if (authState is ResourceState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                )
            }
        }
    }


}


fun isValidEmail(email: String): Boolean {
    return Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matches(email)
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}