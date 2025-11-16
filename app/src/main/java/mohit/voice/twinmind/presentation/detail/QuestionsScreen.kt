package mohit.voice.twinmind.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mohit.voice.twinmind.presentation.record.RecordScreen
import mohit.voice.twinmind.ui.theme.DeepBlue
import mohit.voice.twinmind.ui.theme.MediumGray

@Composable
fun QuestionsScreen(modifier: Modifier = Modifier, navController: NavController) {

    Column(
        modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 🔹 Circle Icon Background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DeepBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.GraphicEq,
                contentDescription = "SoundWave",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Main text
        Text(
            text = "TwinMind is transcribing...",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = DeepBlue
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Description text
        Text(
            text = "You can write your own notes in the notes\n " +
                    "tab or turn off the screen and run in\n" +
                    " background until you click Stop.",
            fontSize = 16.sp,
            color = MediumGray,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center // <-- centered text

        )
    }
}
