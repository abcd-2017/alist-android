package com.android.alist.ui.compose.video

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.view.Surface
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.android.alist.utils.BackHandler
import com.android.alist.utils.NetworkUtils
import com.android.alist.utils.SharePreferenceUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.getStoragePermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.R)
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoPage(
    navController: NavHostController,
    imageName: String,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    videoViewModel: VideoViewModel = hiltViewModel()
) {
    val path by remember {
        mutableStateOf(
            SharePreferenceUtils.getData(
                AppConstant.IMAGE_PATH,
                ""
            )
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val storagePermission = getStoragePermissions()
    //获取当前屏幕状态
    val configuration = LocalConfiguration.current
    //获取activity
    val activity = (LocalContext.current as ComponentActivity)

    LaunchedEffect(configuration.orientation) {
        //横屏时状态栏隐藏并透明
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //状态栏显示
            activity.window.insetsController?.apply {
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                show(android.view.WindowInsets.Type.systemBars())

            }
        } else {
            //自动隐藏状态栏，下拉出现后过一段时间自动隐藏
            activity.window.insetsController?.apply {
                systemBarsBehavior =
                    android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(android.view.WindowInsets.Type.systemBars())

            }
        }
    }


    var mediaItem: MediaItem
    //启动时加载视频url
    LaunchedEffect(Unit) {
        NetworkUtils.getImagePath("$path/$imageName") {
            videoViewModel.videoUrl.value = it
            coroutineScope.launch(Dispatchers.Main) {
                videoViewModel.player.prepare()
                videoViewModel.player.play()
                mediaItem = MediaItem.fromUri(videoViewModel.videoUrl.value)
                videoViewModel.player.setMediaItem(mediaItem)
            }
        }
    }

    BackHandler(backDispatcher = onBackPressedDispatcher) {
        videoViewModel.player.release()
        navController.navigateUp()
    }

    PlayerSurface {
        it.player = videoViewModel.player
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PlayerSurface(
    onPlayerViewAvailable: (PlayerView) -> Unit = {}
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = true
                onPlayerViewAvailable(this)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(Color.Black)
    )
}