package mohit.voice.twinmind.gemini

import com.google.ai.client.generativeai.GenerativeModel
import mohit.voice.twinmind.viewmodel.notes.GeminiApi

class GeminiApiImpl(
    apiKey: String
) : GeminiApi {

    private val model = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    override suspend fun generateSummary(text: String): String {
        val prompt = """
            Summarize the following voice note into a clear and well-organized summary.
            Maintain accuracy and avoid adding extra assumptions.

            Text:
            $text
        """.trimIndent()

        val response = model.generateContent(prompt)
        return response.text ?: "Summary unavailable"
    }
}
