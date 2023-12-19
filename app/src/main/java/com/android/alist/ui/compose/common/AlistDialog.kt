package com.android.alist.ui.compose.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex

/**
 * 弹出窗
 */
@Composable
fun AlistDialog(
    isShowPop: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isShowPop.value) {
        Surface(
            modifier = modifier
                .fillMaxSize()
                //控件层级
                .zIndex(100f),
            color = Color.Black.copy(.5f)
        ) {
            AnimatedVisibility(visible = isShowPop.value) {
                content()
            }
        }
    }
}

@Composable
fun AlistAlertDialog(
    confirmCLick: () -> Unit,
    dismissClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = dismissClick,
        title = { Text(text = "提示") },
        confirmButton = {
            Button(confirmCLick) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            Button(dismissClick) {
                Text(text = "取消")
            }
        },
        text = content
    )
}