package com.loopy.android.data.midi;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LoopyMidiManager_Factory implements Factory<LoopyMidiManager> {
  private final Provider<Context> contextProvider;

  public LoopyMidiManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LoopyMidiManager get() {
    return newInstance(contextProvider.get());
  }

  public static LoopyMidiManager_Factory create(Provider<Context> contextProvider) {
    return new LoopyMidiManager_Factory(contextProvider);
  }

  public static LoopyMidiManager newInstance(Context context) {
    return new LoopyMidiManager(context);
  }
}
