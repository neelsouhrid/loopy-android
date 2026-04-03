package com.loopy.android.di

import android.content.Context
import com.loopy.android.data.audio.AudioEngine
import com.loopy.android.data.datastore.SessionDataStore
import com.loopy.android.data.midi.MidiManager
import com.loopy.android.data.export.ExportManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSessionDataStore(
        @ApplicationContext context: Context
    ): SessionDataStore {
        return SessionDataStore(context)
    }

    @Provides
    @Singleton
    fun provideMidiManager(
        @ApplicationContext context: Context
    ): MidiManager {
        return MidiManager(context)
    }

    @Provides
    @Singleton
    fun provideAudioEngine(): AudioEngine {
        return AudioEngine()
    }

    @Provides
    @Singleton
    fun provideExportManager(
        @ApplicationContext context: Context
    ): ExportManager {
        return ExportManager(context)
    }
}