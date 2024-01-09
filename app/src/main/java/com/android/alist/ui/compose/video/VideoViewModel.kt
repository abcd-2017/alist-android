package com.android.alist.ui.compose.video

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import com.android.alist.App
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class VideoViewModel @Inject constructor() : ViewModel() {
    //视频路径
    var videoUrl = mutableStateOf("")
    lateinit var player: ExoPlayer

    init {
        player = ExoPlayer.Builder(App.context)
            .setLoadControl(
                DefaultLoadControl.Builder().setBufferDurationsMs(
                    // 设置预加载上限下限
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS / 10,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS / 10
                ).build()
            )
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }
}