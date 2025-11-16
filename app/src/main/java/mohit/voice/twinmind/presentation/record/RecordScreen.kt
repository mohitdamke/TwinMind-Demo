package mohit.voice.twinmind.presentation.record

import android.Manifest
import android.R.id.tabs
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mohit.voice.twinmind.presentation.detail.NotesScreen
import mohit.voice.twinmind.presentation.detail.QuestionsScreen
import mohit.voice.twinmind.presentation.detail.TranscriptScreen
import mohit.voice.twinmind.room.dao.MeetingDao
import mohit.voice.twinmind.room.entity.MeetingEntity
import mohit.voice.twinmind.ui.theme.DeepBlue
import mohit.voice.twinmind.ui.theme.MediumGray
import mohit.voice.twinmind.viewmodel.NotesProcessViewModel
import java.io.File
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun RecordScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    meetingDao: MeetingDao,
    notesViewModel: NotesProcessViewModel,
    currentMeetingId: Long = 0L
) {
    val scope = rememberCoroutineScope()
    val context = navController.context

    val currentRecordingId by notesViewModel::currentRecordingId

    var isRecording by remember { mutableStateOf(false) }
    var timerText by remember { mutableStateOf("00:00") }
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    var audioFilePath by remember { mutableStateOf("") }
    var startTime by remember { mutableLongStateOf(0L) }

    // Permissions
    val recordPermission = Manifest.permission.RECORD_AUDIO
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) Toast.makeText(context, "Microphone permission denied", Toast.LENGTH_SHORT)
            .show()
    }
    var isPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                recordPermission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    LaunchedEffect(Unit) { if (!isPermissionGranted) permissionLauncher.launch(recordPermission) }

// Reset for new recording
    LaunchedEffect(currentMeetingId) {
        if (currentMeetingId == 0L) {
            notesViewModel.currentRecordingId = 0L
        } else {
            // Load existing recording if needed
            val meeting = meetingDao.getMeetingById(currentMeetingId)
            notesViewModel.currentRecordingId = meeting?.id ?: 0L
        }
    }


    // Timer
    LaunchedEffect(isRecording) {
        if (isRecording) {
            startTime = System.currentTimeMillis()
            while (isRecording) {
                val elapsed = System.currentTimeMillis() - startTime
                val minutes = (elapsed / 1000) / 60
                val seconds = (elapsed / 1000) % 60
                timerText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                kotlinx.coroutines.delay(500)
            }
        }
    }
    // Load saved meeting duration if viewing an existing recording
    LaunchedEffect(currentMeetingId) {
        if (currentMeetingId > 0L) {
            val meeting = meetingDao.getMeetingById(currentMeetingId)
            timerText = meeting?.duration ?: "00:00"
        }
    }

    // State
    val viewState by notesViewModel.state.collectAsState()

    // State holders
    var transcriptText by remember { mutableStateOf("") }
    var summaryText by remember { mutableStateOf("") }

// Get flows from DAO
    val transcriptFlow = if (currentMeetingId > 0L) {
        notesViewModel.transcriptDao.getTranscriptFlow(currentMeetingId)
    } else {
        notesViewModel.getTranscriptFlow(currentMeetingId)
    }

    val summaryFlow = if (currentMeetingId > 0L) {
        notesViewModel.summaryDao.getSummaryFlow(currentMeetingId)
    } else {
        notesViewModel.getSummaryFlow(currentMeetingId)
    }

// Collect flows as state
    val transcriptEntity by transcriptFlow.collectAsState(initial = null)
    val summaryEntity by summaryFlow.collectAsState(initial = null)

// Update local states
    LaunchedEffect(transcriptEntity, summaryEntity) {
        transcriptText = transcriptEntity?.transcriptText ?: ""
        summaryText = summaryEntity?.summaryText ?: ""
    }


    var meetingTitle by remember { mutableStateOf("") }
    var meetingDate by remember { mutableStateOf("") }

    // ✅ Load meeting info if editing/viewing existing recording
    LaunchedEffect(currentMeetingId) {
        if (currentMeetingId > 0L) {
            val meeting = meetingDao.getMeetingById(currentMeetingId)
            meetingTitle = meeting?.title ?: ""
            meetingDate = meeting?.createdAt?.let {
                android.text.format.DateFormat.format(
                    "dd MMM yyyy hh:mm a",
                    it
                ) as String
            } ?: ""
        }
    }
    val isNewRecording = currentRecordingId == 0L


    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Questions", "Notes", "Transcript")

    Scaffold(
        topBar = { RecordTopBar(timerText, navController) },
        bottomBar = {
            if (isNewRecording) { // only show bottom bar for new recordings

                StopRecordingButton(
                    isRecording = isRecording,
                    onToggleRecording = {

                        if (!isRecording) {
                            if (!isPermissionGranted) {
                                permissionLauncher.launch(recordPermission)
                                return@StopRecordingButton
                            }
                            // Start recording
                            val dir = context.cacheDir
                            val file =
                                File.createTempFile(
                                    "audio_${System.currentTimeMillis()}",
                                    ".m4a",
                                    dir
                                )
                            audioFilePath = file.absolutePath
                            mediaRecorder = MediaRecorder(context).apply {
                                setAudioSource(MediaRecorder.AudioSource.MIC)
                                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                                setOutputFile(audioFilePath)
                                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                                prepare()
                                start()
                            }
                            isRecording = true
                        } else {
                            // Stop recording
                            mediaRecorder?.apply { stop(); release() }
                            mediaRecorder = null
                            isRecording = false

                            // Save to RoomDB
                            scope.launch {
                                val meeting = MeetingEntity(
                                    title = "Meeting ${System.currentTimeMillis()}",
                                    audioPath = audioFilePath,
                                    duration = timerText,
                                    createdAt = System.currentTimeMillis()
                                )
                                val meetingId = meetingDao.insertMeeting(meeting)
                                notesViewModel.saveRecording(
                                    audioPath = audioFilePath,
                                    duration = timerText,
                                    title = meeting.title!!,
                                    meetingId = meetingId
                                )
                                // ✅ Update currentRecordingId so UI reacts
                                notesViewModel.currentRecordingId = meetingId
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                meetingTitle.ifEmpty { "Listening and taking notes..." },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DeepBlue,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(4.dp))
            if (meetingDate.isNotEmpty()) Text(
                "Date: $meetingDate",
                fontSize = 14.sp,
                color = MediumGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Recording Time: $timerText",
                fontSize = 14.sp,
                color = MediumGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(4.dp))
// Determine if processing text/summary (only for new recordings)
            val isProcessing =
                isNewRecording && viewState !is NotesProcessViewModel.NotesState.Completed


            Spacer(Modifier.height(4.dp))

            when {
                currentMeetingId == 0L && viewState is NotesProcessViewModel.NotesState.ConvertingToText ->
                    Text(
                        "⏳ Converting speech to text...",
                        fontSize = 14.sp,
                        color = MediumGray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                currentMeetingId == 0L && viewState is NotesProcessViewModel.NotesState.GeneratingSummary ->
                    Text(
                        "💡 Generating summary...",
                        fontSize = 14.sp,
                        color = DeepBlue,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                !isProcessing ->
                    Text(
                        "✔ Done — switch to Transcript or Notes tab to view result",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
            }

            Spacer(Modifier.height(16.dp))

// Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                contentColor = DeepBlue,
                containerColor = White,
                divider = { HorizontalDivider(thickness = 2.dp, color = Color.Transparent) },
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(
                            tabPositions[selectedTab]
                        ), height = 3.dp, color = DeepBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                        selectedContentColor = DeepBlue,
                        unselectedContentColor = MediumGray
                    )
                }
            }

// Tab content
            when (selectedTab) {
                0 -> QuestionsScreen(modifier = Modifier.weight(1f), navController = navController)
                1 -> NotesScreen(
                    modifier = Modifier.weight(1f),
                    navController = navController,
                    text = summaryText,
                    isProcessing = isProcessing
                )

                2 -> TranscriptScreen(
                    modifier = Modifier.weight(1f),
                    navController = navController,
                    text = transcriptText,
                    isProcessing = isProcessing
                )
            }

        }
    }
}


@Composable
fun StopRecordingButton(
    isRecording: Boolean,
    onToggleRecording: () -> Unit
) {
    Button(
        onClick = onToggleRecording,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRecording) Color.Red else Color(0xFFFFE5E5),
            contentColor = if (isRecording) Color.White else Color(0xFFE01F1F)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(16.dp)
    ) {
        Icon(
            Icons.Default.StopCircle,
            contentDescription = "Record",
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isRecording) "Stop Recording" else "Start Recording",
            fontSize = 18.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordTopBar(timerText: String, navController: NavController) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Red, CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Recording Time: $timerText",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .padding(start = 2.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Back",
                    tint = DeepBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(Modifier.width(1.dp))
                Text("Back", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = DeepBlue)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

