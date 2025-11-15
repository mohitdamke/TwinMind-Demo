package mohit.voice.twinmind.presentation.detail

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import mohit.voice.twinmind.ui.theme.DeepBlue
import mohit.voice.twinmind.ui.theme.MediumGray
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Example summary text — will be copied/shared
    val summaryText = "Here will be summary text..."

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Summary title + Actions buttons (Copy + Share)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Summary",
                fontSize = 20.sp,
                color = DeepBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Copy Button
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Summary copied!")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy summary",
                    tint = DeepBlue
                )
            }

            // Share Button
            IconButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, summaryText)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Summary"))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share summary",
                    tint = DeepBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    Column(modifier = Modifier.padding(4.dp)) {
        // 🔹 Summary body
        Text(
            text = summaryText,
            color = MediumGray,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
        // 🔹 Snackbar feedback
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Preview(showBackground = true)
@Composable
fun NotesScreenPreview() {
    val navController = rememberNavController()
    NotesScreen(navController = navController)
}
