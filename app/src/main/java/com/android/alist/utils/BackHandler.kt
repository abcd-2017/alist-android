package com.android.alist.utils

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

/**
 * 临时禁用返回操作
 */
@Composable
fun BackHandler(
    backDispatcher: OnBackPressedDispatcher,
    onBack: () -> Unit
) {
    val currentOnBack by rememberUpdatedState(onBack)

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                currentOnBack()
            }
        }
    }

    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(backCallback)

        onDispose {
            backCallback.remove()
        }
    }
}