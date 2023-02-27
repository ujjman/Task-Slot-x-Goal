package com.intern.taskslotxgoal

sealed class Screen(val route: String) {
    object MainScreen : Screen(route = "mainScreen")
}