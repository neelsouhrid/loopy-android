package com.loopy.android.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for app-level dependencies.
 * 
 * All core classes (SessionDataStore, LoopyMidiManager, AudioEngine, ExportManager)
 * use @Inject constructor with @Singleton, so Hilt handles their binding automatically.
 * This module is kept for any future custom bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule