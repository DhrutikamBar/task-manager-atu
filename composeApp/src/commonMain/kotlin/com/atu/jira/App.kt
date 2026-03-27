package com.atu.jira

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.atu.jira.auth.AuthManager
import com.atu.jira.model.*
import com.atu.jira.repo.updateTicketWithHistory
import com.atu.jira.screens.*
import kotlinx.coroutines.launch


@Composable
@Preview
fun App(onNavControllerCreated: (NavHostController) -> Unit = {}) {
    val navController = rememberNavController()

    // Notify the platform (Web) that the controller is ready
    LaunchedEffect(navController) {
        onNavControllerCreated(navController)
    }



    NavHost(
        navController = navController,
        startDestination = if (AuthManager.isLoggedIn()) HomeRoute else LoginRoute
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                },
                onSignupClick = { navController.navigate(SignupRoute) }
            )
        }

        composable<EditTicketRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<EditTicketRoute>()
            val scope = rememberCoroutineScope()

           /* var updateTrigger by remember { mutableStateOf(false) }

            LaunchedEffect(updateTrigger) {
                if (updateTrigger) {
                    try {

                    } catch (e: Exception) {
                        println("Update failed: ${e.message}")
                    } finally {
                        updateTrigger = false
                    }
                }
            }*/

            EditTicketScreen(
                ticket = Ticket(
                    id = route.id,
                    title = route.title,
                    description = route.description,
                    status = route.status,
                    projectId = 0L,
                    assignedTo = ""
                ),
                onTicketUpdated = { ticket ->

                    navController.popBackStack()
                }
            )
        }

        composable<SignupRoute> {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<SignupRoute> { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() }
            )
        }

        composable<HomeRoute> {
            HomeScreen(
                onProjectsClick = { project ->
                    navController.navigate(BoardRoute(project.id ?: 0L, project.name))
                },
                onTasksClick = { /* logic for tasks */ },
                onAddProject = { navController.navigate(CreateProjectRoute) },
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable<BoardRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<BoardRoute>()
            TicketBoardScreen(
                project = Project(id = route.projectId, name = route.projectName),
                onTicketClick = { ticket ->
                    navController.navigate(
                        TicketDetailRoute(
                            id = ticket.id!!,
                            title = ticket.title,
                            description = ticket.description,
                            assignedTo = ticket.assignedTo
                        )
                    )
                },
                onBack = { navController.popBackStack() },
                onAddTicket = {
                    navController.navigate(CreateTicketRoute(route.projectId, route.projectName))
                },
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable<CreateTicketRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<CreateTicketRoute>()
            CreateTicketScreen(
                project = Project(id = route.projectId, name = route.projectName),
                onCreate = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable<CreateProjectRoute> {
            CreateProjectScreen(
                onCreate = { project ->
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable<TicketDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TicketDetailRoute>()
            TicketDetailScreenV4(
                ticket = Ticket(
                    id = route.id,
                    title = route.title,
                    description = route.description,
                    status = "", 
                    projectId = 0L,
                    assignedTo = route.assignedTo
                ),
                onBack = { navController.popBackStack() },
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(LoginRoute) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }, onClickEditTicket = { ticket ->
                    navController.navigate(
                        EditTicketRoute(
                            ticket.id.toString(),
                            ticket.title,
                            ticket.description,
                            ticket.status
                        )
                    )
                }
            )
        }
    }
}






