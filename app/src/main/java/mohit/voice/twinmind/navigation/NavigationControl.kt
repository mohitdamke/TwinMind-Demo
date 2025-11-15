package mohit.voice.twinmind.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mohit.voice.twinmind.presentation.home.HomeScreen
import mohit.voice.twinmind.presentation.record.RecordScreen
import mohit.voice.twinmind.presentation.splash.SplashScreen
import mohit.voice.twinmind.viewmodel.notes.NotesProcessViewModel

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun NavigationControl(modifier: Modifier = Modifier, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreen,
    ) {
        composable(Routes.HomeScreen) {
            HomeScreen(modifier, navController)
        }
        composable(Routes.SplashScreen) {
            SplashScreen(modifier, navController)
        }
        composable(Routes.RecordScreen) {
            val notesViewModel: NotesProcessViewModel = hiltViewModel()
            val meetingDao = notesViewModel.meetingDao
            RecordScreen(
                modifier = modifier,
                navController = navController,
                meetingDao = meetingDao,
                notesViewModel = notesViewModel
            )
        }
    }
}