package mohit.voice.twinmind.viewmodel.notes

interface GeminiApi {
    suspend fun generateSummary(text: String): String
}
