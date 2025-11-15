package mohit.voice.twinmind.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import mohit.voice.twinmind.ui.theme.DeepBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDemoScreen(modifier: Modifier = Modifier, navController: NavController) {

    /* Top navbar consist of back button and back text side to back button and
     title will be consist of red circle recording icon and number as 00:01
     */

    // Screen will be consist of
    /*
        Title as Listening and taking notes... (Bold text color = DeepBlue, font size = 22.sp)

        next in column there will be 3 tabs
        1). Questions
        2). Notes
        3). Transcript

     */

    /*

        Bottom bar will be consist of Stop Recording button with full width Recording stop icon in red
        stop text by side and content color will be red and container color will be light red


     */


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🔴 Recording dot
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.Red, CircleShape)
                        )

                        Spacer(Modifier.width(6.dp))

                        Text(
                            text = "00:01",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                },
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Back", fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },

        bottomBar = {
            Button(
                onClick = { /* Stop Recording */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFE5E5),
                    contentColor = Color.Red
                )
            ) {
                Icon(
                    imageVector = Icons.Default.StopCircle,
                    contentDescription = "Stop Recording",
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Recording", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Title
            Text(
                text = "Listening and taking notes...",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Tabs — Questions | Notes | Transcript
            var selectedIndex by remember { mutableIntStateOf(0) }
            val tabs = listOf("Questions", "Notes", "Transcript")

            TabRow(
                selectedTabIndex = selectedIndex,
                contentColor = DeepBlue
            ) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        text = {
                            Text(
                                text = text,
                                fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔹 Screen content according to selected tab
            when (selectedIndex) {
                0 -> Text("Questions content goes here", modifier = Modifier.padding(16.dp))
                1 -> Text("Notes content goes here", modifier = Modifier.padding(16.dp))
                2 -> Text("Transcript content goes here", modifier = Modifier.padding(16.dp))
            }
        }
    }


}


@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    val navController = rememberNavController()   // fake nav controller for preview
    RecordDemoScreen(navController = navController)
}
