package mohit.voice.twinmind.di

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mohit.voice.twinmind.BuildConfig
import mohit.voice.twinmind.gemini.GeminiApiImpl
import mohit.voice.twinmind.gemini.SpeechToTextManagerImpl
import mohit.voice.twinmind.viewmodel.notes.GeminiApi
import mohit.voice.twinmind.viewmodel.notes.SpeechToTextManager
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    fun provideGeminiApi(): GeminiApi {
        val apiKey = BuildConfig.GEMINI_API_KEY   // put your api key here or load from local.properties
        return GeminiApiImpl(apiKey)
    }

    @Provides
    @Singleton
    fun provideSpeechToTextManager(
        @ApplicationContext context: Context
    ): SpeechToTextManager {
        return SpeechToTextManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient()
}
