package com.android.alist.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.android.alist.utils.constant.AppConstant
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * 权限申请工具
 *
 * permissions: 需要申请的权限列表
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsUtil(permissions: List<String>) {
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)

    if (multiplePermissionsState.allPermissionsGranted) {
        Log.d(AppConstant.APP_NAME, "RequestPermissionsUtil: 权限已全部申请")
    } else {
        LaunchedEffect(Unit) {
            multiplePermissionsState.launchMultiplePermissionRequest()
        }
    }
}