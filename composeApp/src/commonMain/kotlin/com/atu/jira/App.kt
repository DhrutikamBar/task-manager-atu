package com.atu.jira

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.atu.jira.auth.AuthManager
import com.atu.jira.model.*
import com.atu.jira.screens.*
import com.atu.jira.theme.JiraTheme

@Composable
@Preview
fun App(onNavControllerCreated: (NavHostController) -> Unit = {}) {
    val navController = rememberNavController()

    // Notify the platform (Web) that the controller is ready
    LaunchedEffect(navController) {
        onNavControllerCreated(navController)
    }

    JiraTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
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

                    EditTicketScreen(
                        ticket = Ticket(
                            id = route.id,
                            title = route.title,
                            description = route.description,
                            status = route.status,
                            priority = route.priority,
                            projectId = route.projectId,
                            assignedTo = route.assignedTo,
                            createdBy = route.createdBy,
                            startTime = route.startTime,
                            endTime = route.endTime,
                            dueDate = route.dueDate,
                            createdAt = route.createdAt
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
                        onTasksClick = { /* logic for tasks handled internally in HomeScreen */ },
                        onAddProject = { navController.navigate(CreateProjectRoute) },
                        onLogout = {
                            AuthManager.logout()
                            navController.navigate(LoginRoute) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        },
                        onTaskClick = { ticket ->
                            navController.navigate(
                                TicketDetailRoute(
                                    id = ticket.id!!,
                                    title = ticket.title,
                                    description = ticket.description,
                                    status = ticket.status,
                                    priority = ticket.priority,
                                    assignedTo = ticket.assignedTo,
                                    projectId = ticket.projectId,
                                    createdBy = ticket.createdBy,
                                    startTime = ticket.startTime,
                                    endTime = ticket.endTime,
                                    dueDate = ticket.dueDate,
                                    createdAt = ticket.createdAt
                                )
                            )
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
                                    status = ticket.status,
                                    priority = ticket.priority,
                                    assignedTo = ticket.assignedTo,
                                    projectId = ticket.projectId,
                                    createdBy = ticket.createdBy,
                                    startTime = ticket.startTime,
                                    endTime = ticket.endTime,
                                    dueDate = ticket.dueDate,
                                    createdAt = ticket.createdAt
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
                            status = route.status, 
                            priority = route.priority,
                            projectId = route.projectId,
                            assignedTo = route.assignedTo,
                            createdBy = route.createdBy,
                            startTime = route.startTime,
                            endTime = route.endTime,
                            dueDate = route.dueDate,
                            createdAt = route.createdAt
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
                                    id = ticket.id.toString(),
                                    title = ticket.title,
                                    description = ticket.description,
                                    status = ticket.status,
                                    priority = ticket.priority,
                                    assignedTo = ticket.assignedTo,
                                    projectId = ticket.projectId,
                                    createdBy = ticket.createdBy,
                                    startTime = ticket.startTime,
                                    endTime = ticket.endTime,
                                    dueDate = ticket.dueDate,
                                    createdAt = ticket.createdAt
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
