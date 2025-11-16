package mohit.voice.twinmind.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import mohit.voice.twinmind.R
import mohit.voice.twinmind.navigation.Routes
import mohit.voice.twinmind.room.entity.MeetingEntity
import mohit.voice.twinmind.ui.theme.DeepBlue
import mohit.voice.twinmind.ui.theme.MediumGray
import mohit.voice.twinmind.ui.theme.SoftGray
import mohit.voice.twinmind.viewmodel.NotesProcessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    notesViewModel: NotesProcessViewModel = hiltViewModel()
) {
    val meetings = notesViewModel.meetings.collectAsState().value

    LaunchedEffect(Unit) {
        notesViewModel.getAllMeetings()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mohit's TwinMind",
                        color = DeepBlue,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.boyprofile),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Routes.recordScreen(0L)) },
                    colors = ButtonColors(
                        containerColor = DeepBlue,
                        contentColor = Color.White,
                        disabledContainerColor = DeepBlue,
                        disabledContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "mic_icon",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Capture Notes")
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(4.dp)
        ) {

            if (meetings.isEmpty()) {
                Text(
                    text = "No recordings yet",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    color = MediumGray
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(meetings) { meeting ->
                        MeetingItem(meeting) {
                            navController.navigate(Routes.recordScreen(meetingId = meeting.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MeetingItem(meeting: MeetingEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, SoftGray, CircleShape)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                contentDescription = "FileIcon",
                tint = DeepBlue,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftGray, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Text(
                text = meeting.title ?: "Untitled Recording",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = DeepBlue
            )

            val time = android.text.format.DateFormat.format("hh:mm a", meeting.createdAt)
            Text(
                text = "${meeting.duration} • $time",
                fontSize = 14.sp,
                color = MediumGray
            )
        }
    }
}
