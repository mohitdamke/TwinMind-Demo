package mohit.voice.twinmind.gemini

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mohit.voice.twinmind.R
import mohit.voice.twinmind.viewmodel.notes.SpeechToTextManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.google.auth.oauth2.GoogleCredentials
import java.io.File
import javax.inject.Inject

class SpeechToTextManagerImpl @Inject constructor(
    private val context: Context
) : SpeechToTextManager {

    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val stream = context.resources.openRawResource(R.raw.speech_key)
        val credentials = GoogleCredentials.fromStream(stream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        credentials.refreshIfExpired()
        val token = credentials.accessToken.tokenValue
        Log.d("SpeechDebug", "Access token acquired")
        token
    }

    private fun encodeAudioToBase64(audioPath: String): String {
        val file = File(audioPath)
        Log.d("SpeechDebug", "Recorded audio file path: $audioPath")
        Log.d("SpeechDebug", "Recorded audio file size = ${file.length()} bytes")
        val audioBytes = file.readBytes()
        return Base64.encodeToString(audioBytes, Base64.NO_WRAP)
    }

    override suspend fun convertAudioToText(audioPath: String): String = withContext(Dispatchers.IO) {
        Log.d("SpeechDebug", "🔄 Starting Speech-to-Text conversion...")

        val accessToken = getAccessToken()

        val sampleRate = getAudioSampleRate(audioPath)
        Log.d("SpeechDebug", "🎯 Detected sample rate = $sampleRate Hz")

        val inputFile = File(audioPath)
        val wavFile = File(context.cacheDir, "converted_${System.currentTimeMillis()}.wav")

        // Convert AAC/M4A → WAV PCM using SAME sample rate
        AudioConverter.convertM4aToWav(inputFile, wavFile, sampleRate) { success ->
            Log.d("SpeechDebug", "Audio convert success: $success")
        }

        Log.d("SpeechDebug", "Converted WAV size = ${wavFile.length()} bytes")

        val base64Audio = Base64.encodeToString(wavFile.readBytes(), Base64.NO_WRAP)

        // 👇 sampleRate injected properly (no more fixed 16000)
        val json = """
    {
      "config": {
        "encoding": "LINEAR16",
        "sampleRateHertz": $sampleRate,
        "languageCode": "en-US",
        "enableAutomaticPunctuation": true
      },
      "audio": { "content": "$base64Audio" }
    }
    """.trimIndent()

        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://speech.googleapis.com/v1/speech:recognize")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(body)
            .build()

        val response = OkHttpClient().newCall(request).execute()
        val responseString = response.body?.string() ?: ""

        Log.d("SpeechDebug", "🔍 Full API Response: $responseString")

        val jsonObj = JSONObject(responseString)
        val results = jsonObj.optJSONArray("results")

        if (results == null || results.length() == 0) {
            Log.e("SpeechDebug", "❌ Google returned no transcription")
            return@withContext ""
        }

        val transcript = jsonObj
            .optJSONArray("results")
            ?.optJSONObject(0)
            ?.optJSONArray("alternatives")
            ?.optJSONObject(0)
            ?.optString("transcript")
            ?: ""

        Log.d("SpeechDebug", "📝 Transcription received: $transcript")
        return@withContext transcript
    }


    private fun getAudioSampleRate(audioPath: String): Int {
        val extractor = MediaExtractor()
        extractor.setDataSource(audioPath)
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                return format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            }
        }
        return 44100 // fallback
    }
}
