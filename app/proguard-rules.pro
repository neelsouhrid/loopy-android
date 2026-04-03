# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the default ProGuard configuration.

# Keep model classes
-keep class com.loopy.android.domain.model.** { *; }

# Keep data classes for JSON serialization
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# MIDI
-keep class android.media.midi.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Compose
-dontwarn androidx.compose.**

# DataStore
-keep class androidx.datastore.** { *; }