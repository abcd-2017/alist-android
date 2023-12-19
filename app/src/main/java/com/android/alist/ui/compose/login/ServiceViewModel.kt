package com.android.alist.ui.compose.login

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotMutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.alist.App
import com.android.alist.database.table.Service
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.getLocalTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor() : ViewModel() {
    //当前选择service
    private val currentService =
        mutableStateOf(Service(null, "", 0, AppConstant.Default.FALSE.value, "", "", "", ""))

    //是否显示删除弹窗
    val showRemovePop = mutableStateOf(false)

    //当前选中的服务器的id
    val selectId = mutableIntStateOf(-1)

    //是否显示修改页面
    val showEditPop = mutableStateOf(false)

    //是否显示连接服务器弹窗页面
    val showConnectPop = mutableStateOf(false)

    //当前是否添加
    val isAdd = mutableStateOf(true)

    //是否开启加载动画
    val isLoading = mutableStateOf(false)

    //是否显示按钮展开的悬浮窗
    val floatButtonState = mutableStateOf(false)

    //是否在刷新状态
    var isRefreshing by mutableStateOf(false)
        private set

    //输入框内容
    val inputIp = mutableStateOf("")
    val inputPort = mutableStateOf("")
    val inputUsername = mutableStateOf("")
    val inputPassword = mutableStateOf("")
    val isDefault = mutableIntStateOf(AppConstant.Default.FALSE.value)
    val inputDescription = mutableStateOf("")
    val ipError = mutableStateOf(false)
    private val _serviceList = MutableStateFlow<List<Service>>(emptyList())
    val serviceList = _serviceList.asStateFlow()

    private val serviceDao = App.db.getServiceDao()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getServerList()
        }
    }

    //刷新服务列表
    fun refreshServiceList(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            _serviceList.value = emptyList()
            getServerList()
            delay(2500)
            isRefreshing = false
            launch(Dispatchers.Main) {
                callback()
            }
        }
    }

    //查询所有服务列表
    private suspend fun getServerList() {
        _serviceList.value = serviceDao.queryAll()
    }

    //清除input输入内容
    fun clearInput() {
        inputIp.value = ""
        inputPort.value = ""
        inputUsername.value = ""
        inputPassword.value = ""
        isDefault.intValue = AppConstant.Default.FALSE.value
        inputDescription.value = ""
        ipError.value = false
    }

    //添加服务
    fun addService() {
        defaultServiceSetValue()

        viewModelScope.launch(Dispatchers.IO) {
            if (currentService.value.isDefault == AppConstant.Default.TRUE.value) {
                clearDefaultValue()
            }
            serviceDao.insert(currentService.value)
            getServerList()
            clearService()
        }
    }

    fun getServiceById(): Service? {
        serviceList.value.forEach { it ->
            if (it.id == selectId.value) {
                return it
            }
        }
        return null
    }

    fun updateService() {
        defaultServiceSetValue()

        viewModelScope.launch(Dispatchers.IO) {
            if (currentService.value.isDefault == AppConstant.Default.TRUE.value) {
                clearDefaultValue()
            }
            serviceDao.update(currentService.value)
            getServerList()
            clearService()
        }
    }

    fun deleteService() {
        viewModelScope.launch(Dispatchers.IO) {
            currentService.value.id = selectId.value
            serviceDao.deleteById(currentService.value)
            getServerList()
            selectId.value = -1
        }
    }

    fun setServiceValue(service: Service): Boolean {
        return if (service.id != selectId.value) {
            false
        } else {
            inputIp.value = service.ip
            inputPort.value = service.port.toString()
            inputUsername.value = service.username
            inputPassword.value = service.password
            inputDescription.value = service.description.toString()
            isDefault.intValue = service.isDefault
            true
        }
    }

    private fun clearDefaultValue() {
        serviceDao.clearDefault(
            defaultValue = AppConstant.Default.FALSE.value,
            notDefaultValue = AppConstant.Default.TRUE.value
        )
    }

    private fun defaultServiceSetValue() {
        if (inputUsername.value.isEmpty()) inputUsername.value = AppConstant.DEFAULT_USERNAME
        if (inputPassword.value.isEmpty()) inputPassword.value = AppConstant.DEFAULT_PASSWORD
        if (inputPort.value.isEmpty()) inputPort.value = AppConstant.DEFAULT_PORT.toString()

        if (showEditPop.value && selectId.value != -1)
            currentService.value.id = selectId.value
        currentService.value.username = inputUsername.value
        currentService.value.password = inputPassword.value
        currentService.value.port = inputPort.value.toInt()
        currentService.value.isDefault = isDefault.intValue
        currentService.value.ip = inputIp.value
        currentService.value.description = inputDescription.value
        currentService.value.updateDate = getLocalTime()
    }

    private fun clearService() {
        currentService.value =
            Service(null, "", 0, AppConstant.Default.FALSE.value, "", "", "", "")
        isDefault.value = AppConstant.Default.FALSE.value
    }
}