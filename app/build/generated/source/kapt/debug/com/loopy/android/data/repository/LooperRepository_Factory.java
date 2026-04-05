package com.loopy.android.data.repository;

import com.loopy.android.data.audio.AudioEngine;
import com.loopy.android.data.datastore.SessionDataStore;
import com.loopy.android.data.export.ExportManager;
import com.loopy.android.data.midi.LoopyMidiManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class LooperRepository_Factory implements Factory<LooperRepository> {
  private final Provider<SessionDataStore> sessionDataStoreProvider;

  private final Provider<LoopyMidiManager> midiManagerProvider;

  private final Provider<AudioEngine> audioEngineProvider;

  private final Provider<ExportManager> exportManagerProvider;

  public LooperRepository_Factory(Provider<SessionDataStore> sessionDataStoreProvider,
      Provider<LoopyMidiManager> midiManagerProvider, Provider<AudioEngine> audioEngineProvider,
      Provider<ExportManager> exportManagerProvider) {
    this.sessionDataStoreProvider = sessionDataStoreProvider;
    this.midiManagerProvider = midiManagerProvider;
    this.audioEngineProvider = audioEngineProvider;
    this.exportManagerProvider = exportManagerProvider;
  }

  @Override
  public LooperRepository get() {
    return newInstance(sessionDataStoreProvider.get(), midiManagerProvider.get(), audioEngineProvider.get(), exportManagerProvider.get());
  }

  public static LooperRepository_Factory create(Provider<SessionDataStore> sessionDataStoreProvider,
      Provider<LoopyMidiManager> midiManagerProvider, Provider<AudioEngine> audioEngineProvider,
      Provider<ExportManager> exportManagerProvider) {
    return new LooperRepository_Factory(sessionDataStoreProvider, midiManagerProvider, audioEngineProvider, exportManagerProvider);
  }

  public static LooperRepository newInstance(SessionDataStore sessionDataStore,
      LoopyMidiManager midiManager, AudioEngine audioEngine, ExportManager exportManager) {
    return new LooperRepository(sessionDataStore, midiManager, audioEngine, exportManager);
  }
}
