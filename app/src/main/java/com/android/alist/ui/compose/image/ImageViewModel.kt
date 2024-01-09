package com.android.alist.ui.compose.image

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor() : ViewModel() {
    //图片路径
    var imageUrl = mutableStateOf("")

    //tap栏是否可见
    val showTap = mutableStateOf(true)
}