package mohit.voice.twinmind.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mohit.voice.twinmind.BuildConfig
import mohit.voice.twinmind.gemini.GeminiApiImpl
import mohit.voice.twinmind.gemini.SpeechToTextManagerImpl
import mohit.voice.twinmind.viewmodel.notes.GeminiApi
import mohit.voice.twinmind.viewmodel.notes.SpeechToTextManager
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
    fun provideSpeechToTextManager(): SpeechToTextManager {
        val apiKey = BuildConfig.GEMINI_API_KEY  // safer than hardcoding
        return SpeechToTextManagerImpl(apiKey)
    }
}
