package com.android.alist.ui.compose.file

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.android.alist.App
import com.android.alist.network.RetrofitClient
import com.android.alist.network.api.FsApi
import com.android.alist.network.entity.ResponseData
import com.android.alist.network.entity.fs.File
import com.android.alist.network.to.fs.FsFileListTo
import com.android.alist.network.to.fs.FsRemoveFileTo
import com.android.alist.network.to.fs.FsRenameTo
import com.android.alist.utils.NetworkUtils
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

    //修改文件名
    val editFileName = mutableStateOf(TextFieldValue(text = "", selection = TextRange.Zero))

    //当前选中的文件index
    val selectFileOldName = mutableStateOf("")

    //修改文件弹窗
    val showEditFileName = mutableStateOf(false)

    //删除文件弹窗
    val showDeleteFileName = mutableStateOf(false)

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

    //下载文件
    fun downloadFile(context: Context, fileName: String, callback: (message: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            NetworkUtils.downloadFile(context, getPath(), fileName, callback)
        }
    }

    //修改文件名
    fun updateFileName(callback: (result: ResponseData<Unit>) -> Unit) {
        if (selectFileOldName.value != editFileName.value.text) {
            viewModelScope.launch(Dispatchers.IO) {
                val responseData = fsApi.renameFile(
                    FsRenameTo(
                        name = editFileName.value.text,
                        path = "${getPath()}/${selectFileOldName.value}"
                    )
                )
                launch(Dispatchers.Main) { callback(responseData) }

                selectFileOldName.value = ""
                showEditFileName.value = false
                getFileList()
            }
        }
    }

    //删除文件或文件夹
    fun deleteFile(callback: (result: ResponseData<Unit>) -> Unit) {
        if (selectFileOldName.value.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val responseData = fsApi.removeFile(
                    FsRemoveFileTo(
                        dir = getPath(),
                        names = listOf(selectFileOldName.value)
                    )
                )
                launch(Dispatchers.Main) { callback(responseData) }

                selectFileOldName.value = ""
                showDeleteFileName.value = false
                getFileList()
            }
        }
    }

    fun getFileList() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileEntity = fsApi.fileList(FsFileListTo(null, null, getPath(), null, null))
            _fileList.value = if (fileEntity.data?.content != null) {
                fileEntity.data.content
            } else emptyList()
        }
    }

    private fun getPath(): String {
        val buffer = StringBuffer("")
        pathList.forEach { buffer.append("/").append(it) }
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