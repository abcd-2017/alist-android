package com.android.alist.ui.compose.file

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.android.alist.App
import com.android.alist.network.RetrofitClient
import com.android.alist.network.api.FsApi
import com.android.alist.network.entity.fs.File
import com.android.alist.network.to.fs.FsFileListTo
import com.android.alist.utils.constant.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor() : ViewModel() {
    private lateinit var fsApi: FsApi
    private val serviceDao = App.db.getServiceDao()

    //文件路径
    val path = mutableStateOf("")

    //是否在刷新状态
    val isRefresh = mutableStateOf(false)

    //所有文件信息
    private var _fileList = MutableStateFlow<List<File>>(emptyList())
    var fileList = _fileList.asStateFlow()
    val pathList = mutableStateListOf<String>()

    fun initParam(navController: NavHostController) {
        viewModelScope.launch(Dispatchers.IO) {
            fsApi = RetrofitClient(App.context, serviceDao).getRequestApi(FsApi::class.java)

            getFileList()
        }
    }

    fun getFileList() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileEntity = fsApi.fileList(FsFileListTo(null, null, getPath(), null, null))
            _fileList.value = if (fileEntity.data != null) {
                fileEntity.data.content
            } else emptyList()
        }
    }

    private fun getPath(): String {
        val buffer = StringBuffer("")
        pathList.forEach { buffer.append("/").append(it) }
        Log.d(AppConstant.APP_NAME, "getPath: ${buffer.toString()}")
        return buffer.toString()
    }

    fun RefreshFileList() {
        viewModelScope.launch {
            isRefresh.value = true
            delay(1600)
            getFileList()
            isRefresh.value = false
        }
    }
}