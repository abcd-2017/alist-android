package com.android.alist.ui.compose.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.alist.database.table.Service
import com.android.alist.state.ScreenState
import com.android.alist.ui.compose.common.AlistAlertDialog
import com.android.alist.ui.compose.common.AlistDialog
import com.android.alist.ui.compose.common.RowSpacer
import com.android.alist.utils.BackHandler
import com.android.alist.utils.NetworkUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.convertTimeToDisplayString
import com.android.alist.utils.dragTheActionTool
import com.android.alist.utils.verifyAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * 选择alist服务器页面
 */
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ServicePage(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    context: Context = LocalContext.current,
    serviceViewModel: ServiceViewModel = hiltViewModel()
) {
    val keyBoard = LocalSoftwareKeyboardController.current
    val current = LocalConfiguration.current

    val screenState = if (current.screenHeightDp >= current.screenWidthDp) {
        ScreenState.PORTRAIT_SCREEN
    } else {
        ScreenState.LANDSCAPE_SCREEN
    }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold() { paddingValues ->
            //服务器列表
            ServiceList(
                serviceList = serviceViewModel.serviceList.collectAsState().value,
                screenState = screenState,
                current = current,
                modifier = Modifier.padding(paddingValues),
                isRefreshing = serviceViewModel.isRefreshing,
                isRemove = serviceViewModel.showRemovePop,
                isEdit = serviceViewModel.showEditPop,
                isConnect = serviceViewModel.showConnectPop,
                selectId = serviceViewModel.selectId,
                isAdd = serviceViewModel.isAdd,
                setDefaultValue = { it ->
                    if (!serviceViewModel.setServiceValue(it)) {
                        Toast.makeText(context, "格式异常", Toast.LENGTH_SHORT).show()
                    }
                },
                onRefresh = {
                    serviceViewModel.refreshServiceList {
                        Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            //弹出窗集合
            ShowPop(
                isRemove = serviceViewModel.showRemovePop,
                isEdit = serviceViewModel.showEditPop,
                isConnect = serviceViewModel.showConnectPop,
                selectId = serviceViewModel.selectId,
                context = context,
                isAdd = serviceViewModel.isAdd,
                inputIp = serviceViewModel.inputIp,
                inputPort = serviceViewModel.inputPort,
                inputUsername = serviceViewModel.inputUsername,
                inputPassword = serviceViewModel.inputPassword,
                inputIsDefault = serviceViewModel.isDefault,
                inputDescription = serviceViewModel.inputDescription,
                onBackPressedDispatcher = onBackPressedDispatcher,
                ipError = serviceViewModel.ipError,
                isLoading = serviceViewModel.isLoading,
                onClick = {
                    if (verifyAddress(serviceViewModel.inputIp.value)) {
                        serviceViewModel.updateService()
                        Toast.makeText(context, "修改成功", Toast.LENGTH_LONG).show()
                        serviceViewModel.showEditPop.value = false

                        serviceViewModel.clearInput()
                        keyBoard?.hide()

                    } else {
                        serviceViewModel.ipError.value = true
                        Toast.makeText(context, "ip地址格式错误", Toast.LENGTH_SHORT).show()
                    }
                },
                onClosePage = {
                    serviceViewModel.showEditPop.value = false
                    keyBoard?.hide()
                    serviceViewModel.clearInput()
                },
                onRemove = { serviceViewModel.deleteService() },
                onLoadingService = {
                    val service = serviceViewModel.getServiceById()

                    if (service == null) {
                        Toast.makeText(context, "参数异常", Toast.LENGTH_SHORT).show()
                        serviceViewModel.isLoading.value = false
                    } else {
                        coroutineScope.launch(Dispatchers.IO) {
                            var showText = ""

                            delay(1000)

                            if (NetworkUtils.isConnectable(service.ip, service.port)) {
                                showText = "连接成功"
                            } else {
                                showText = "该服务器无法连接"
                            }

                            launch(Dispatchers.Main) {
                                Toast.makeText(context, showText, Toast.LENGTH_SHORT).show()
                                serviceViewModel.isLoading.value = false
                            }
                        }
                    }
                }
            )
        }
    }

    //悬浮按钮，可展开成一个页面
    if (!serviceViewModel.showEditPop.value) {
        ConstraintLayout {
            val floatButtonBox = createRef()

            Box(modifier = Modifier.constrainAs(floatButtonBox) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }) {
                FloatingButtonContent(
                    floatButtonState = serviceViewModel.floatButtonState,
                    isAdd = serviceViewModel.isAdd,
                    onBackPressedDispatcher = onBackPressedDispatcher,
                    inputIp = serviceViewModel.inputIp,
                    inputPort = serviceViewModel.inputPort,
                    inputUsername = serviceViewModel.inputUsername,
                    inputPassword = serviceViewModel.inputPassword,
                    inputIsDefault = serviceViewModel.isDefault,
                    inputDescription = serviceViewModel.inputDescription,
                    ipError = serviceViewModel.ipError,
                    onClick = {
                        if (verifyAddress(serviceViewModel.inputIp.value)) {
                            serviceViewModel.addService()
                            Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show()

                            serviceViewModel.floatButtonState.value = false
                            serviceViewModel.clearInput()
                            keyBoard?.hide()
                        } else {
                            serviceViewModel.ipError.value = true
                            Toast.makeText(context, "ip地址格式错误", Toast.LENGTH_SHORT).show()
                        }
                    }, onClosePage = {
                        serviceViewModel.floatButtonState.value = false
                        keyBoard?.hide()
                        serviceViewModel.clearInput()
                    }
                )
            }
        }
    }
}

/**
 * 该控件所有弹出窗
 */
@Composable
fun ShowPop(
    isRemove: MutableState<Boolean>,
    isEdit: MutableState<Boolean>,
    isConnect: MutableState<Boolean>,
    selectId: MutableIntState,
    context: Context,
    isAdd: MutableState<Boolean>,
    inputIp: MutableState<String>,
    inputPort: MutableState<String>,
    inputUsername: MutableState<String>,
    inputPassword: MutableState<String>,
    inputIsDefault: MutableState<Int>,
    inputDescription: MutableState<String>,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    ipError: MutableState<Boolean>,
    onClick: () -> Unit,
    onClosePage: () -> Unit,
    onRemove: () -> Unit,
    isLoading: MutableState<Boolean>,
    onLoadingService: () -> Unit
) {
    //删除服务器弹出窗
    AlistDialog(isShowPop = isRemove) {
        AlistAlertDialog(confirmCLick = {
            if (selectId.value == -1) {
                Toast.makeText(context, "参数异常", Toast.LENGTH_SHORT).show()

            } else {
                onRemove()
                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
            }
            isRemove.value = false
        },
            dismissClick = {
                selectId.value = -1
                isRemove.value = false
            }) {
            Text(text = "是否删除服务器？")
        }
    }

    //修改服务器
    AnimatedVisibility(visible = isEdit.value) {
        Surface(color = MaterialTheme.colorScheme.background) {
            LevitationNewPage(
                isAdd = isAdd,
                inputIp = inputIp,
                inputPort = inputPort,
                onBackPressedDispatcher = onBackPressedDispatcher,
                inputUsername = inputUsername,
                inputPassword = inputPassword,
                inputIsDefault = inputIsDefault,
                onClick = onClick,
                inputDescription = inputDescription,
                ipError = ipError,
                onClosePage = onClosePage
            )
        }
    }

    //是否连接该服务器
    AlistDialog(isShowPop = isConnect) {
        AlistAlertDialog(
            confirmCLick = {
                isLoading.value = true
                isConnect.value = false
            },
            dismissClick = { isConnect.value = false }) {
            Text(text = "是否连接到该服务器")
        }
    }

    //连接服务器
    AlistDialog(isShowPop = isLoading) {
        AlertDialog(onDismissRequest = { }, title = {
            Text(text = "连接中")
        }, confirmButton = { }, text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    strokeWidth = 4.dp
                )
            }

            onLoadingService()
        })
    }
}

/**
 * 悬浮按钮内容
 */
@Composable
fun FloatingButtonContent(
    floatButtonState: MutableState<Boolean>,
    isAdd: MutableState<Boolean>,
    inputIp: MutableState<String>,
    inputPort: MutableState<String>,
    inputUsername: MutableState<String>,
    inputPassword: MutableState<String>,
    inputIsDefault: MutableState<Int>,
    inputDescription: MutableState<String>,
    ipError: MutableState<Boolean>,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    onClick: () -> Unit,
    onClosePage: () -> Unit
) {
    val transition =
        updateTransition(targetState = floatButtonState.value, "FloatButtonState")
    val current = LocalConfiguration.current

    val boxWidth by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = Spring.StiffnessVeryLow
            )
        }, label = "width"
    ) {
        if (it) current.screenWidthDp.dp else 56.dp
    }

    val boxHeight by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = Spring.StiffnessVeryLow
            )
        }, label = "height"
    ) {
        if (it) current.screenHeightDp.dp else 56.dp
    }

    val boxMargin by transition.animateDp(
        transitionSpec = {
            spring(stiffness = Spring.StiffnessVeryLow)
        }, label = "boxMargin"
    ) {
        if (it) 0.dp else 20.dp
    }

    var currentPage by remember {
        mutableStateOf(AppConstant.ShowContentPage.Control.currentPage)
    }
    val currentHeight = remember { mutableStateOf(current.screenHeightDp.dp) }

    currentPage = if (
        if (boxHeight > boxWidth) boxHeight < current.screenHeightDp.dp / 4
        else boxWidth < current.screenWidthDp.dp / 4
    ) {
        AppConstant.ShowContentPage.Control.currentPage
    } else {
        AppConstant.ShowContentPage.Page.currentPage
    }

    AnimatedContent(
        targetState = currentPage, label = "page",
        transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(220, delayMillis = 90)
                    )
                    ).togetherWith(fadeOut(animationSpec = tween(90)))
        }
    ) {
        when (it) {
            AppConstant.ShowContentPage.Control.currentPage -> {
                Card(
                    modifier = Modifier.padding(boxMargin),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp, pressedElevation = 3.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .width(boxWidth)
                            .height(boxHeight)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                floatButtonState.value = true
                                isAdd.value = true
                            },
                        Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            AppConstant.ShowContentPage.Page.currentPage -> {
                Box(
                    modifier = Modifier
                        .height(boxHeight)
                        .width(boxWidth)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AnimatedVisibility(
                        visible = boxHeight > currentHeight.value * .999f,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        LevitationNewPage(
                            isAdd = isAdd,
                            onBackPressedDispatcher = onBackPressedDispatcher,
                            inputIp = inputIp,
                            inputPort = inputPort,
                            inputUsername = inputUsername,
                            inputPassword = inputPassword,
                            inputIsDefault = inputIsDefault,
                            onClick = onClick,
                            inputDescription = inputDescription,
                            ipError = ipError,
                            onClosePage = onClosePage
                        )
                    }
                }
            }
        }
    }
}

/**
 * 由按钮展开的新页面
 */
@Composable
fun LevitationNewPage(
    isAdd: MutableState<Boolean>,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    inputIp: MutableState<String>,
    inputPort: MutableState<String>,
    inputUsername: MutableState<String>,
    inputPassword: MutableState<String>,
    inputIsDefault: MutableState<Int>,
    inputDescription: MutableState<String>,
    ipError: MutableState<Boolean>,
    onClick: () -> Unit,
    onClosePage: () -> Unit
) {
    //临时禁用返回手势
    BackHandler(onBackPressedDispatcher) {
        //手势返回时，关闭临时页面
        onClosePage()
    }

    Column(
        Modifier
            .padding(horizontal = 10.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .size(44.dp)
                    .clickable { onClosePage() },
                Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }

            Text(
                text = if (isAdd.value) "添加服务" else "修改服务",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, top = 5.dp)
            )
        }

        LazyColumn() {
            item {
                var showPassword by remember {
                    mutableStateOf(false)
                }

                val portFocusRequester = remember {
                    FocusRequester()
                }
                val ipFocusRequester = remember {
                    FocusRequester()
                }

                LaunchedEffect(Unit) {
                    ipFocusRequester.requestFocus()
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .addDropActionIf(isAdd.value, onClosePage)
                )


                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = inputIp.value,
                        onValueChange = {
                            if (it == "") {
                                inputIp.value = it
                            } else if (it[it.length - 1].isDigit() || it[it.length - 1] == '.') {
                                inputIp.value = it
                            }

                            if (ipError.value) {
                                ipError.value = !verifyAddress(it)
                            }
                        },
                        label = { Text(text = "IP") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        isError = ipError.value,
                        keyboardActions = KeyboardActions(
                            onNext = {
                                ipError.value = if (verifyAddress(inputIp.value)) {
                                    portFocusRequester.requestFocus()
                                    false
                                } else {
                                    true
                                }
                            }
                        ),
                        modifier = Modifier.focusRequester(ipFocusRequester)
                    )

                    if (ipError.value) {
                        Text(
                            text = "IP地址格式错误", color = Color.Red
                        )
                    }

                    RowSpacer(10)

                    OutlinedTextField(
                        value = inputPort.value,
                        onValueChange = {
                            if (it[it.length - 1].isDigit() && it.toInt() <= AppConstant.MAX_PORT) {
                                inputPort.value = it
                            }
                        },
                        placeholder = {
                            Text(text = "5244")
                        },
                        label = { Text(text = "Port") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(),
                        modifier = Modifier.focusRequester(portFocusRequester)
                    )

                    RowSpacer(10)

                    OutlinedTextField(
                        value = inputUsername.value,
                        onValueChange = {
                            inputUsername.value = it
                        },
                        placeholder = {
                            Text(text = "admin")
                        },
                        label = { Text(text = "用户名") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )

                    RowSpacer(10)

                    OutlinedTextField(
                        value = inputPassword.value,
                        onValueChange = {
                            inputPassword.value = it
                        },
                        label = { Text(text = "密码") },
                        singleLine = true,
                        placeholder = {
                            Text(text = "admin")
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            Icon(
                                imageVector = if (showPassword) Icons.Default.PanoramaFishEye else Icons.Default.RemoveRedEye,
                                contentDescription = null,
                                Modifier.clickable { showPassword = !showPassword }
                            )
                        }
                    )

                    RowSpacer(10)

                    OutlinedTextField(
                        value = inputDescription.value,
                        onValueChange = {
                            inputDescription.value = it
                        },
                        label = { Text(text = "描述") },
                        maxLines = 3
                    )

                    RowSpacer(10)

                    Row(
                        modifier = Modifier
                            .width(400.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "默认服务器")

                        Switch(
                            checked = inputIsDefault.value == AppConstant.Default.TRUE.value,
                            onCheckedChange = {
                                inputIsDefault.value =
                                    if (inputIsDefault.value == AppConstant.Default.TRUE.value) {
                                        AppConstant.Default.FALSE.value
                                    } else {
                                        AppConstant.Default.TRUE.value
                                    }
                            }
                        )
                    }

                    RowSpacer(40)

                    Button(
                        modifier = Modifier.width(280.dp),
                        onClick = onClick,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = if (isAdd.value) "添加" else "修改")
                    }

                    RowSpacer(100)
                }
            }
        }
    }
}

/**
 * 当condition为true是，才给控件加上拖拽关闭方法
 */
fun Modifier.addDropActionIf(condition: Boolean, onClosePage: () -> Unit): Modifier {
    return if (condition) {
        this.then(Modifier.dragTheActionTool(onClosePage))
    } else this
}

/**
 * 可选服务器列表
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ServiceList(
    serviceList: List<Service>,
    screenState: ScreenState,
    modifier: Modifier = Modifier,
    current: Configuration,
    isRefreshing: Boolean,
    isRemove: MutableState<Boolean>,
    isEdit: MutableState<Boolean>,
    isConnect: MutableState<Boolean>,
    isAdd: MutableState<Boolean>,
    selectId: MutableIntState,
    onRefresh: () -> Unit,
    setDefaultValue: (Service) -> Unit
) {
    val pullRefreshState =
        rememberPullRefreshState(isRefreshing, onRefresh = onRefresh)

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (serviceList.isNotEmpty()) {
            val lazyListState = rememberLazyListState()

            RowSpacer(10)

            Row(
                Modifier
                    .height(36.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "选择alist服务器",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            RowSpacer(10)

            Box(Modifier.pullRefresh(pullRefreshState)) {
                LazyColumn(
                    state = lazyListState
                ) {
                    val dataList = when (screenState) {
                        ScreenState.PORTRAIT_SCREEN -> serviceList
                        ScreenState.LANDSCAPE_SCREEN -> serviceList.chunked(2)
                    }
                    items(dataList) {
                        when (screenState) {
                            ScreenState.PORTRAIT_SCREEN -> {
                                ServiceCard(
                                    service = it as Service,
                                    isRemove = isRemove,
                                    isEdit = isEdit,
                                    isConnect = isConnect,
                                    selectId = selectId,
                                    isAdd = isAdd,
                                    setDefaultValue = setDefaultValue,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 3.dp)
                                )
                            }

                            ScreenState.LANDSCAPE_SCREEN -> {
                                val list = it as List<*>

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 36.dp),
                                    horizontalArrangement = if (list.size % 2 == 0) Arrangement.SpaceBetween else Arrangement.Start
                                ) {
                                    list.forEach {
                                        ServiceCard(
                                            service = it as Service,
                                            isRemove = isRemove,
                                            isEdit = isEdit,
                                            isConnect = isConnect,
                                            selectId = selectId,
                                            isAdd = isAdd,
                                            setDefaultValue = setDefaultValue,
                                            modifier = Modifier
                                                .width(current.screenWidthDp.dp * .46f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

        } else {
            //如果服务器列表为空，显示文字
            Row(
                modifier = modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "暂无服务器，请添加alist服务器",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * 展示服务信息卡片
 */
@Composable
fun ServiceCard(
    service: Service,
    isRemove: MutableState<Boolean>,
    isEdit: MutableState<Boolean>,
    isConnect: MutableState<Boolean>,
    isAdd: MutableState<Boolean>,
    modifier: Modifier,
    selectId: MutableIntState,
    setDefaultValue: (Service) -> Unit
) {
    val containerColor = when (service.isDefault) {
        AppConstant.Default.TRUE.value -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val textColor = when (service.isDefault) {
        AppConstant.Default.TRUE.value -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }
    //是否显示下拉菜单
    var dropDownState by remember {
        mutableStateOf(false)
    }
    //手指点击位置的偏移量
    val animatedOffset = remember {
        Animatable(Offset(0f, 0f), Offset.VectorConverter)
    }
    val coroutineScope = rememberCoroutineScope()

    CompositionLocalProvider(LocalTextStyle provides TextStyle(color = textColor)) {
        AnimatedVisibility(visible = true) {
            Card(
                modifier = modifier
                    .padding(horizontal = 10.dp, vertical = 3.dp)
                    .wrapContentHeight()
                    .pointerInput(Unit) {
                        //手势操作
                        detectTapGestures(
                            onTap = {
                                //点击，弹出弹窗确定是否连接
                                selectId.value = if (service.id != null) service.id!! else -1
                                isConnect.value = true
                            },
                            onLongPress = {
                                //长按弹出选择菜单
                                dropDownState = true
                                coroutineScope.launch {
                                    animatedOffset.snapTo(it)
                                }
                            }
                        )
                    },
                colors = CardDefaults.cardColors(
                    containerColor = containerColor,
                ), elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp, pressedElevation = 3.dp
                )
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        Modifier.padding(horizontal = 16.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .height(40.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${service.ip}:${service.port}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Row(
                            Modifier.height(30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (service.description != null && service.description!!.isNotEmpty()) {
                                Text(text = "${service.description}")
                            } else {
                                Text(text = "暂无描述")
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                    }
                    Column {
                        Row(
                            modifier = Modifier
                                .height(40.dp)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = convertTimeToDisplayString(service.updateDate),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            //下拉菜单
            Box(
                modifier = Modifier.offset {
                    IntOffset(
                        animatedOffset.value.x.roundToInt(),
                        animatedOffset.value.y.roundToInt()
                    )
                }) {
                DropdownMenu(
                    expanded = dropDownState,
                    onDismissRequest = { dropDownState = false }
                ) {
                    DropPopItem(
                        text = "删除",
                        onClick = {
                            selectId.value = if (service.id != null) service.id!! else -1
                            dropDownState = false
                            isRemove.value = true
                        }
                    )

                    DropPopItem(
                        text = "修改",
                        onClick = {
                            selectId.value = if (service.id != null) service.id!! else -1
                            dropDownState = false
                            isAdd.value = false
                            isEdit.value = true
                            setDefaultValue(service)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DropPopItem(
    text: String,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            text = {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            onClick = onClick,
        )
    }
}