package com.android.alist.ui.compose.image

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.android.alist.utils.NetworkUtils
import com.android.alist.utils.SharePreferenceUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.getStoragePermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePage(
    navController: NavHostController,
    imageName: String,
    imageViewModel: ImageViewModel = hiltViewModel()
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

    LaunchedEffect(Unit) {
        NetworkUtils.getImagePath("$path/$imageName") {
            imageViewModel.imageUrl.value = it
        }
    }

    Scaffold(topBar = {
        AnimatedVisibility(imageViewModel.showTap.value) {
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.background,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null,
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            if (!storagePermission.allPermissionsGranted) {
                                storagePermission.launchMultiplePermissionRequest()
                            } else {
                                Toast.makeText(context, "开始下载...", Toast.LENGTH_SHORT).show()
                                NetworkUtils.downloadFile(context, path, imageName) {
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            it,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Outlined.CloudDownload, contentDescription = null)
                    }
                },
                title = {
                    Text(
                        text = imageName,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                })
        }
    }) { paddingValues ->
        ImageBrowser(Modifier.padding(paddingValues), imageViewModel.imageUrl.value) {
            imageViewModel.showTap.value = !imageViewModel.showTap.value
        }
    }

}

@Composable
fun ImageBrowser(modifier: Modifier, imageUrl: String, onClick: () -> Unit) {
    //缩放比例
    var scale by remember { mutableStateOf(1f) }
    //偏移量
    var offset by remember { mutableStateOf(Offset.Zero) }
    //监听手势状态变换
    val state = rememberTransformableState() { zoomChange, _, rotationChange ->
        scale = (zoomChange * scale).coerceAtLeast(1f)
        scale = if (scale > 5f) 5f else scale
    }

    Surface(
        modifier = modifier
            .fillMaxSize(),
        color = Color.Black
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .graphicsLayer { //布局缩放、旋转、移动变换
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
                .transformable(state)
                .pointerInput(offset) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale <= 1f) 2f else 1f
                            offset = Offset.Zero
                        }, onTap = {
                            onClick()
                        }
                    )
                }
        ) {
            val imageState = painter.state
            if (imageState is AsyncImagePainter.State.Loading || imageState is AsyncImagePainter.State.Error) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                SubcomposeAsyncImageContent(modifier = Modifier)
            }
        }
    }
}