package com.loopy.android.ui.components;

import androidx.compose.foundation.ExperimentalFoundationApi;
import androidx.compose.runtime.Composable;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.loopy.android.domain.model.Track;
import androidx.compose.foundation.lazy.grid.GridCells;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000B\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aV\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001aq\u0010\u000f\u001a\u00020\u00012\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00030\u00112\u0006\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u00132\b\u0010\u0015\u001a\u0004\u0018\u00010\u00132\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u00172\u0012\u0010\u0018\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\u00172\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u00a2\u0006\u0002\u0010\u0019\u00a8\u0006\u001a"}, d2 = {"TrackButton", "", "track", "Lcom/loopy/android/domain/model/Track;", "isSelected", "", "isPlaying", "isRecording", "playbackPosition", "", "onClick", "Lkotlin/Function0;", "onLongClick", "modifier", "Landroidx/compose/ui/Modifier;", "TrackGrid", "tracks", "", "selectedIndex", "", "playingIndex", "recordingIndex", "onTrackClick", "Lkotlin/Function1;", "onTrackLongClick", "(Ljava/util/List;ILjava/lang/Integer;Ljava/lang/Integer;FLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;)V", "app_debug"})
public final class TrackComponentsKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.ExperimentalFoundationApi.class})
    @androidx.compose.runtime.Composable()
    public static final void TrackButton(@org.jetbrains.annotations.NotNull()
    com.loopy.android.domain.model.Track track, boolean isSelected, boolean isPlaying, boolean isRecording, float playbackPosition, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLongClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void TrackGrid(@org.jetbrains.annotations.NotNull()
    java.util.List<com.loopy.android.domain.model.Track> tracks, int selectedIndex, @org.jetbrains.annotations.Nullable()
    java.lang.Integer playingIndex, @org.jetbrains.annotations.Nullable()
    java.lang.Integer recordingIndex, float playbackPosition, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onTrackClick, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onTrackLongClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}