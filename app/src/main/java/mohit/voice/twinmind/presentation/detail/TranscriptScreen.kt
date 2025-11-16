package mohit.voice.twinmind.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mohit.voice.twinmind.ui.theme.DeepBlue
import mohit.voice.twinmind.ui.theme.MediumGray
import mohit.voice.twinmind.ui.theme.SoftGray

@Composable
fun TranscriptScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    text: String,
    isProcessing: Boolean,
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        if (isProcessing) {
            Text(
                text = "Transcribing audio...",
                fontSize = 18.sp,
                color = DeepBlue
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            return
        }
        // 🕒 Time text
        Text(
            text = "14:25",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 📌 Transcript text
        Text(
            text = text.ifEmpty { "Transcript not available" },
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MediumGray
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ─ Divider
        HorizontalDivider(
            thickness = 1.dp,
            color = SoftGray
        )
    }
}
