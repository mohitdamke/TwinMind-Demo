package mohit.voice.twinmind.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import mohit.voice.twinmind.R
import mohit.voice.twinmind.navigation.Routes
import java.nio.file.WatchEvent

@Composable
fun SplashScreen(modifier: Modifier = Modifier, navController: NavController) {

    LaunchedEffect(Unit) {
        // TODO: Add delay + navigation later
        delay(1000)
        navController.navigate(Routes.HomeScreen) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.twinmind),
            contentDescription = "splash", modifier = Modifier
                .align(Alignment.Center)
                .size(100.dp)
        )
    }
}
