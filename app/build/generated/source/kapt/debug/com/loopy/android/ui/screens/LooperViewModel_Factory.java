package com.loopy.android.ui.screens;

import com.loopy.android.data.repository.LooperRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class LooperViewModel_Factory implements Factory<LooperViewModel> {
  private final Provider<LooperRepository> looperRepositoryProvider;

  public LooperViewModel_Factory(Provider<LooperRepository> looperRepositoryProvider) {
    this.looperRepositoryProvider = looperRepositoryProvider;
  }

  @Override
  public LooperViewModel get() {
    return newInstance(looperRepositoryProvider.get());
  }

  public static LooperViewModel_Factory create(
      Provider<LooperRepository> looperRepositoryProvider) {
    return new LooperViewModel_Factory(looperRepositoryProvider);
  }

  public static LooperViewModel newInstance(LooperRepository looperRepository) {
    return new LooperViewModel(looperRepository);
  }
}
