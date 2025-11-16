package mohit.voice.twinmind

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.core.app.ServiceCompat.stopForeground
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
class RecordService : Service() {

    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String = ""
    private var isRecording = false
    private var startTime = 0L

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var audioManager: AudioManager
    private var wasPausedByCall = false
    private var wasPausedByFocus = false

    private val notificationId = 999
    private val channelId = "record_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Phone call listener
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when(state) {
                    TelephonyManager.CALL_STATE_RINGING,
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        if (isRecording) pauseRecording("Paused - Phone call")
                        wasPausedByCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (wasPausedByCall) resumeRecording()
                        wasPausedByCall = false
                    }
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when(action) {
            "START" -> startRecording()
            "STOP" -> stopRecording()
            "PAUSE" -> pauseRecording("Paused")
            "RESUME" -> resumeRecording()
        }
        return START_STICKY
    }

    private fun startRecording() {
        if (isRecording) return

        if (checkLowStorage()) {
            stopSelf()
            return
        }

        val dir = cacheDir
        val file = File.createTempFile("audio_${System.currentTimeMillis()}", ".m4a", dir)
        audioFilePath = file.absolutePath

        mediaRecorder = MediaRecorder(this).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }

        isRecording = true
        startTime = System.currentTimeMillis()
        startForeground(notificationId, buildNotification("Recording..."))

        // Start timer coroutine
        CoroutineScope(Dispatchers.Default).launch {
            while (isRecording) {
                updateNotification()
                detectSilence()
                delay(1000)
            }
        }
    }

    private fun pauseRecording(reason: String) {
        if (!isRecording) return
        mediaRecorder?.pause()
        isRecording = false
        updateNotification(reason)
    }

    private fun resumeRecording() {
        if (isRecording) return
        mediaRecorder?.resume()
        isRecording = true
        updateNotification("Recording...")
    }

    private fun stopRecording() {
        mediaRecorder?.apply { stop(); release() }
        mediaRecorder = null
        isRecording = false
        stopForeground(true)
        stopSelf()
        // TODO: save MeetingEntity to Room with audioFilePath & duration
    }

    private fun detectSilence() {
        mediaRecorder?.maxAmplitude?.let { amp ->
            if (amp < 1000) {
                // Add logic: if silent for >10 sec → show warning
            }
        }
    }

    private fun checkLowStorage(): Boolean {
        val free = cacheDir.usableSpace
        return free < 5_000_000 // <5MB → stop recording
    }

    private fun buildNotification(status: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Recording")
            .setContentText(status)
            .setSmallIcon(R.drawable.microphone)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .addAction(R.drawable.pause_button, "Pause", getActionIntent("PAUSE"))
            .addAction(R.drawable.stop_button, "Stop", getActionIntent("STOP"))
            .build()
    }

    private fun getActionIntent(action: String): PendingIntent {
        val intent = Intent(this, RecordService::class.java).apply { this.action = action }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun updateNotification(status: String = "Recording...") {
        val elapsed = System.currentTimeMillis() - startTime
        val minutes = (elapsed / 1000) / 60
        val seconds = (elapsed / 1000) % 60
        val timer = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
        val notification = buildNotification("$status ($timer)")
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(channelId, "Recording", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
