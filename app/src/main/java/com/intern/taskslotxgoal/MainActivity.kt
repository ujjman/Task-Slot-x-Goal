package com.intern.taskslotxgoal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.intern.taskslotxgoal.navigation.SetupNavGraph
import com.intern.taskslotxgoal.ui.theme.TaskSlotXGoalTheme
import com.intern.taskslotxgoal.viewmodelfactories.MainViewModelFactory
import com.intern.taskslotxgoal.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainViewModel: MainViewModel by viewModels {
            MainViewModelFactory()
        }
        setContent {
            TaskSlotXGoalTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController, mainViewModel = mainViewModel
                )
            }
        }
    }
}
