package com.atu.jira

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavController
import androidx.navigation.bindToBrowserNavigation
import com.atu.jira.auth.AuthManager
import com.atu.jira.koin.initKoin
import com.atu.jira.utils.WebSessionStorage
import kotlinx.browser.document // Requires kotlinx-browser dependency
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {

    AuthManager.sessionStorage = WebSessionStorage()
    initKoin()
    // This global flag disables the red 'Error UI' overlay
    // We target the ID string from index.html
    ComposeViewport(viewportContainerId = "ComposeTarget") {
        val scope = rememberCoroutineScope()

        App(onNavControllerCreated = { navController ->
            scope.launch {
                // This will now sync with the browser address bar
                navController.bindToBrowserNavigation()
            }
        })
    }
}

/*@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    val body = document.body ?: return

    ComposeViewport("ComposeTarget") {
        // We wrap the logic in a state-aware block
        var navHostController by remember { mutableStateOf<NavController?>(null) }

        App(onNavControllerCreated = { navController ->
            navHostController = navController
        })

        // When the navController is ready, start the binding in a coroutine
        navHostController?.let { controller ->
            LaunchedEffect(controller) {
                controller.bindToBrowserNavigation()
            }
        }
    }
}*/

/*
@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    AuthManager.sessionStorage = WebSessionStorage()

    // Using simple ComposeViewport which looks for document.body
    */
/*ComposeViewport("ComposeTarget") {
        App()
    }*//*


   */
/* ComposeViewport() {
        App()
    }*//*


    val body = document.body ?: return

    ComposeViewport(body) {
        App(onNavControllerCreated = { navController ->
            navController.bindToBrowserNavigation()
        })
    }
}*/
