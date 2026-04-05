package com.atu.jira.koin

import com.atu.jira.viewmodel.AuthViewModel
import com.atu.jira.viewmodel.ProjectViewModel
import com.atu.jira.viewmodel.TicketViewModel
import org.koin.dsl.module
import org.koin.core.context.startKoin


val appModule = module {
    single { TicketViewModel() }
    single { ProjectViewModel() }
    single { AuthViewModel() }
}

fun initKoin() {
    startKoin {
        modules(appModule)
    }
}