package com.android.alist.ui.compose.file

import android.content.res.Configuration
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.android.alist.network.entity.fs.File
import com.android.alist.utils.FileUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.constant.ScreenWidthConstant
import com.android.alist.utils.formatDateTime
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import com.android.alist.App
import com.android.alist.R
import com.android.alist.ui.compose.PageConstant
import com.android.alist.ui.compose.common.AlistAlertDialog
import com.android.alist.ui.compose.common.AlistDialog
import com.android.alist.ui.compose.common.ColumnSpacer
import com.android.alist.ui.compose.common.DropPopItem
import com.android.alist.ui.compose.common.RowSpacer
import com.android.alist.utils.BackHandler
import com.android.alist.utils.SharePreferenceUtils
import com.android.alist.utils.constant.HttpStatusCode
import com.android.alist.utils.getStoragePermissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FilePage(
    navController: NavHostController,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    fileViewModel: FileViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val fileListState = fileViewModel.fileList.collectAsState()
    val fileScrollState = rememberLazyListState()
    val pathScrollState = rememberLazyListState()
    val context = LocalContext.current
    //获取存储权限
    val storagePermission = getStoragePermissions()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(Unit) {
        fileViewModel.initParam(navController)
    }
    if (fileViewModel.pathList.size > 0) {
        BackHandler(backDispatcher = onBackPressedDispatcher) {
            fileViewModel.pathList.removeLast()
            fileViewModel.getFileList()
        }
    } else if (drawerState.isOpen) {
        BackHandler(backDispatcher = onBackPressedDispatcher) {
            coroutineScope.launch { drawerState.close() }
        }
    }
    // 定义启动文件选择器的结果处理器
    val selectFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { _ ->
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use { cursors ->
                    val nameIndex = cursors.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursors.moveToFirst()
                    val fileName = cursors.getString(nameIndex)
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val file = context.contentResolver.openInputStream(uri)

                            val token = SharePreferenceUtils.getData(AppConstant.TOKEN, "")
                            file?.let {
                                //上传文件
                                fileViewModel.uploadFile(it, fileName, token) { message ->
                                    launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(AppConstant.APP_NAME, "FilePage: $e")
                            launch(Dispatchers.Main) {
                                Toast.makeText(context, "上传错误，$e", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    )

    FilePageContent(
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        fileList = fileListState,
        isRefresh = fileViewModel.isRefresh,
        pathList = fileViewModel.pathList,
        fileScrollState = fileScrollState,
        pathScrollState = pathScrollState,
        snackBarHostState = snackBarHostState,
        selectFileLauncher = selectFileLauncher,
        pathCardClick = { index ->
            if (index == 0) fileViewModel.pathList.clear()
            else fileViewModel.pathList.removeRange(index, fileViewModel.pathList.size)
            fileViewModel.getFileList()
        },
        fileRowOnClick = { file ->
            if (file.is_dir) {
                coroutineScope.launch {
                    fileViewModel.pathList.add(file.name)
                    fileViewModel.getFileList()
                    pathScrollState.scrollToItem(fileViewModel.pathList.size - 1)
                }
            } else if (FileUtils.getSvgCodeByFileType(file.name) == R.drawable.filetype_image) {
                SharePreferenceUtils.saveData(AppConstant.IMAGE_PATH, fileViewModel.getPath())
                navController.navigate("${PageConstant.Image.text}/${file.name}")
            } else if (FileUtils.getSvgCodeByFileType(file.name) == R.drawable.filetype_video) {
                SharePreferenceUtils.saveData(AppConstant.IMAGE_PATH, fileViewModel.getPath())
                navController.navigate("${PageConstant.Video.text}/${file.name}")
            }
        }, onRefresh = {
            fileViewModel.RefreshFileList()
        },
        fileRenameClick = { file ->
            fileViewModel.editFileName.value =
                TextFieldValue(text = file.name, selection = TextRange(file.name.length))
            fileViewModel.showEditFileName.value = true
        }, fileCopyClick = { file ->

        }, fileDownloadClick = { file ->
            //申请权限
            coroutineScope.launch {
                if (!storagePermission.allPermissionsGranted) {
                    storagePermission.launchMultiplePermissionRequest()
                } else {
                    snackBarHostState.showSnackbar(
                        message = "正在下载中...",
                        withDismissAction = true
                    )
                    fileViewModel.downloadFile(context, file.name) {
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
        }, fileDeleteClick = { file ->
            fileViewModel.selectFileOldName.value = file.name
            fileViewModel.showDeleteFileName.value = true
        }, addFolder = {
            fileViewModel.addFolder.value = true
            val fileName = "新建文件夹"
            fileViewModel.editFileName.value =
                TextFieldValue(text = fileName, selection = TextRange(fileName.length))
        }, changeService = {
            //回到选择服务器界面
            SharePreferenceUtils.saveData(AppConstant.TOKEN, "")
            SharePreferenceUtils.saveData(AppConstant.DEFAULT_SERVER, "")
            navController.popBackStack()
            navController.navigate("${PageConstant.Service.text}/true")
        }
    )
    //添加文件夹
    AddFileOrEditFileNameAlert(
        showEditFileName = fileViewModel.addFolder,
        editFileName = fileViewModel.editFileName,
        onDismissRequest = {
            fileViewModel.addFolder.value = false
            fileViewModel.selectFileOldName.value = ""
        },
        doEditName = {
            if (fileViewModel.editFileName.value.text.isEmpty()) {
                Toast.makeText(context, "文件名不能为空", Toast.LENGTH_SHORT).show()
            } else {
                coroutineScope.launch(Dispatchers.IO) {
                    fileViewModel.addFolder { result ->
                        launch(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                if (result.code == HttpStatusCode.Success.code) "添加成功" else "添加失败，${result.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        },
        alertTitle = "新建文件夹"
    )

    //修改文件名
    AddFileOrEditFileNameAlert(
        showEditFileName = fileViewModel.showEditFileName,
        editFileName = fileViewModel.editFileName,
        onDismissRequest = {
            fileViewModel.showEditFileName.value = false
            fileViewModel.selectFileOldName.value = ""
        },
        alertTitle = "重命名",
        doEditName = {
            if (fileViewModel.editFileName.value.text.isEmpty()) {
                Toast.makeText(context, "文件名不能为空", Toast.LENGTH_SHORT).show()
            } else {
                fileViewModel.updateFileName { result ->
                    val showText =
                        if (result.code == HttpStatusCode.Success.code) "修改成功" else "修改失败，${result.message}"

                    Toast.makeText(context, showText, Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    //删除文件名
    AlistDialog(isShowPop = fileViewModel.showDeleteFileName) {
        AlistAlertDialog(
            confirmCLick = {
                fileViewModel.deleteFile { result ->
                    val showText =
                        if (result.code == HttpStatusCode.Success.code) "删除成功" else "删除失败，${result.message}"

                    Toast.makeText(context, showText, Toast.LENGTH_SHORT).show()
                }
            },
            dismissClick = { fileViewModel.showDeleteFileName.value = false },
            title = { Text(text = "提示", color = MaterialTheme.colorScheme.onBackground) }
        ) {
            Text(text = "确定要删除？", color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
fun AddFileOrEditFileNameAlert(
    showEditFileName: MutableState<Boolean>,
    editFileName: MutableState<TextFieldValue>,
    onDismissRequest: () -> Unit,
    doEditName: () -> Unit,
    alertTitle: String
) {
    AlistDialog(isShowPop = showEditFileName) {
        val focusRequester = remember {
            FocusRequester()
        }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        AlistAlertDialog(
            dismissClick = onDismissRequest,
            confirmCLick = doEditName,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = alertTitle, style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                TextField(
                    value = editFileName.value,
                    onValueChange = {
                        editFileName.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ), keyboardActions = KeyboardActions(
                        onDone = {
                            doEditName()
                        }),
                    modifier = Modifier.focusRequester(focusRequester)
                )
            }
        }
    }
}

@Composable
fun FilePageContent(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    fileList: State<List<File>>,
    isRefresh: MutableState<Boolean>,
    pathList: SnapshotStateList<String>,
    addFolder: () -> Unit,
    fileRenameClick: (file: File) -> Unit,
    fileCopyClick: (file: File) -> Unit,
    fileDeleteClick: (file: File) -> Unit,
    fileDownloadClick: (file: File) -> Unit,
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    onRefresh: () -> Unit,
    changeService: () -> Unit,
    fileScrollState: LazyListState,
    pathScrollState: LazyListState,
    snackBarHostState: SnackbarHostState,
    selectFileLauncher: ManagedActivityResultLauncher<String, Uri?>
) {
    val configuration = LocalConfiguration.current

    if (configuration.smallestScreenWidthDp.compareTo(ScreenWidthConstant.Medium.width.value) > 0) {
        PadPage(
            configuration = configuration,
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            fileList = fileList,
            isRefresh = isRefresh,
            pathList = pathList,
            fileScrollState = fileScrollState,
            pathScrollState = pathScrollState,
            snackBarHostState = snackBarHostState,
            addFolder = addFolder,
            selectFileLauncher = selectFileLauncher,
            pathCardClick = pathCardClick,
            fileRowOnClick = fileRowOnClick,
            fileCopyClick = fileCopyClick,
            fileDownloadClick = fileDownloadClick,
            fileRenameClick = fileRenameClick,
            fileDeleteClick = fileDeleteClick,
            onRefresh = onRefresh,
            changeService = changeService
        )
    } else {
        PhonePage(
            configuration = configuration,
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            fileList = fileList,
            isRefresh = isRefresh,
            fileScrollState = fileScrollState,
            pathScrollState = pathScrollState,
            snackBarHostState = snackBarHostState,
            addFolder = addFolder,
            selectFileLauncher = selectFileLauncher,
            pathList = pathList,
            pathCardClick = pathCardClick,
            fileCopyClick = fileCopyClick,
            fileDownloadClick = fileDownloadClick,
            fileRenameClick = fileRenameClick,
            fileDeleteClick = fileDeleteClick,
            fileRowOnClick = fileRowOnClick,
            onRefresh = onRefresh,
            changeService = changeService
        )
    }
}

//平板显示页面
@Composable
fun PadPage(
    configuration: Configuration,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    fileList: State<List<File>>,
    isRefresh: MutableState<Boolean>,
    pathList: SnapshotStateList<String>,
    fileScrollState: LazyListState,
    snackBarHostState: SnackbarHostState,
    pathScrollState: LazyListState,
    selectFileLauncher: ManagedActivityResultLauncher<String, Uri?>,
    addFolder: () -> Unit,
    fileRenameClick: (file: File) -> Unit,
    fileCopyClick: (file: File) -> Unit,
    fileDeleteClick: (file: File) -> Unit,
    fileDownloadClick: (file: File) -> Unit,
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    changeService: () -> Unit,
    onRefresh: () -> Unit,
) {
    val width = Dp(
        if (configuration.screenWidthDp > configuration.screenHeightDp) {
            configuration.screenWidthDp * .23f
        } else {
            configuration.screenWidthDp * .34f
        }
    )

    Row {
        Column {
            DrawerContent(width, changeService)
        }

        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onBackground)
            )
        }

        Column {
            Scaffold(
                topBar = {
                    TopAppBar(
                        backgroundColor = MaterialTheme.colorScheme.background,
                        elevation = 0.dp,
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.DensityMedium,
                                    contentDescription = null
                                )
                            }
                        }, actions = {
                            //操作拦图标
                            IconButton(onClick = { selectFileLauncher.launch("*/*") }) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudUpload,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = addFolder) {
                                Icon(
                                    imageVector = Icons.Outlined.CreateNewFolder,
                                    contentDescription = null
                                )
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = null
                                )

                            }
                        }
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackBarHostState)
                }
            ) { paddingValues ->
                FileList(
                    modifier = Modifier.padding(paddingValues),
                    fileList = fileList,
                    isRefresh = isRefresh,
                    pathList = pathList,
                    fileScrollState = fileScrollState,
                    pathScrollState = pathScrollState,
                    coroutineScope = coroutineScope,
                    onClick = pathCardClick,
                    fileRowOnClick = fileRowOnClick,
                    fileCopyClick = fileCopyClick,
                    fileDownloadClick = fileDownloadClick,
                    fileRenameClick = fileRenameClick,
                    fileDeleteClick = fileDeleteClick,
                    onRefresh = onRefresh,
                )
            }
        }
    }
}

//手机显示页面
@Composable
fun PhonePage(
    configuration: Configuration,
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    fileList: State<List<File>>,
    isRefresh: MutableState<Boolean>,
    pathList: SnapshotStateList<String>,
    fileScrollState: LazyListState,
    pathScrollState: LazyListState,
    snackBarHostState: SnackbarHostState,
    selectFileLauncher: ManagedActivityResultLauncher<String, Uri?>,
    addFolder: () -> Unit,
    fileRenameClick: (file: File) -> Unit,
    fileCopyClick: (file: File) -> Unit,
    fileDeleteClick: (file: File) -> Unit,
    fileDownloadClick: (file: File) -> Unit,
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    changeService: () -> Unit,
    onRefresh: () -> Unit,
) {
    val width = Dp(
        if (configuration.screenWidthDp > configuration.screenHeightDp) {
            configuration.screenWidthDp * .34f
        } else {
            configuration.screenWidthDp * .7f
        }
    )

    ModalNavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
        drawerContent = {
            DrawerContent(width, changeService)
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    elevation = 0.dp,
                    title = {
                        Text(
                            text = AppConstant.APP_NAME,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.DensityMedium,
                                contentDescription = null
                            )
                        }
                    }, actions = {
                        //操作拦图标
                        IconButton(onClick = { selectFileLauncher.launch("*/*") }) {
                            Icon(
                                imageVector = Icons.Outlined.CloudUpload,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = addFolder) {
                            Icon(
                                imageVector = Icons.Outlined.CreateNewFolder,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)

                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState)
            }
        ) { paddingValues ->
            FileList(
                modifier = Modifier.padding(paddingValues),
                fileList = fileList,
                isRefresh = isRefresh,
                pathList = pathList,
                fileScrollState = fileScrollState,
                pathScrollState = pathScrollState,
                coroutineScope = coroutineScope,
                onClick = pathCardClick,
                fileRowOnClick = fileRowOnClick,
                fileCopyClick = fileCopyClick,
                fileDownloadClick = fileDownloadClick,
                fileRenameClick = fileRenameClick,
                fileDeleteClick = fileDeleteClick,
                onRefresh = onRefresh,
            )
        }
    }
}

/**
 * 文件管理页面
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FileList(
    modifier: Modifier,
    fileList: State<List<File>>,
    isRefresh: MutableState<Boolean>,
    pathList: SnapshotStateList<String>,
    coroutineScope: CoroutineScope,
    fileScrollState: LazyListState,
    pathScrollState: LazyListState,
    onClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    fileRenameClick: (file: File) -> Unit,
    fileCopyClick: (file: File) -> Unit,
    fileDeleteClick: (file: File) -> Unit,
    fileDownloadClick: (file: File) -> Unit,
    onRefresh: () -> Unit,
) {
    val pullRefreshState =
        rememberPullRefreshState(isRefresh.value, onRefresh = onRefresh)

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        LazyColumn(state = fileScrollState) {
            //文件路径
            item {
                Row(modifier = Modifier.padding(20.dp, 8.dp)) {
                    PathCard(pathList.size == 0, "/", 0, onClick)
                    if (pathList.size > 0) {
                        LazyRow(state = pathScrollState) {
                            itemsIndexed(pathList) { i, it ->
                                PathCard(
                                    isLast = i == pathList.size - 1,
                                    pathName = it,
                                    index = i + 1,
                                    onClick = onClick
                                )
                            }
                        }
                    }
                }
            }

            if (fileList.value.isNotEmpty()) {
                //文件内容管理
                items(fileList.value) {
                    FileRow(
                        it,
                        coroutineScope = coroutineScope,
                        fileCopyClick = fileCopyClick,
                        fileDownloadClick = fileDownloadClick,
                        fileRenameClick = fileRenameClick,
                        fileDeleteClick = fileDeleteClick,
                        fileRowOnClick = fileRowOnClick,
                    )
                }
            } else {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "没有文件", color = MaterialTheme.colorScheme.onBackground)
                    }
                }

            }
        }

        PullRefreshIndicator(
            refreshing = isRefresh.value,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * 侧拉框页面内容
 */
@Composable
fun DrawerContent(
    width: Dp,
    changeService: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row {
            ColumnSpacer(width = 30)
            Column {
                RowSpacer(height = 20)

                Row {
                    Text(
                        text = "alist",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                RowSpacer(height = 50)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { changeService() }
                        .width(width * .8f)
                        .height(40.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Cloud, contentDescription = null)
                    Text(
                        text = "更换服务器",
                        modifier = Modifier.padding(start = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun FileRow(
    file: File,
    coroutineScope: CoroutineScope,
    fileRowOnClick: (file: File) -> Unit,
    fileRenameClick: (file: File) -> Unit,
    fileCopyClick: (file: File) -> Unit,
    fileDeleteClick: (file: File) -> Unit,
    fileDownloadClick: (file: File) -> Unit,
) {
    //是否显示下拉菜单
    var dropDownState by remember {
        mutableStateOf(false)
    }
    //手指点击位置的偏移量
    val animatedOffset = remember {
        Animatable(Offset(0f, 0f), Offset.VectorConverter)
    }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .pointerInput(file) {
                detectTapGestures(onLongPress = {
                    App.vibratorHelper.vibrateOnLongPress()
                    dropDownState = true
                    coroutineScope.launch {
                        animatedOffset.snapTo(it)
                    }
                }, onTap = { fileRowOnClick(file) })
            }
    ) {
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp),
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(FileUtils.getSvgCodeByFileType(if (file.is_dir) "folder" else file.name))
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    imageLoader = ImageLoader.Builder(LocalContext.current)
                        .components { add(SvgDecoder.Factory()) }
                        .crossfade(true)
                        .build()),
                contentDescription = null)

            Column(
                Modifier
                    .padding(start = 10.dp, end = 10.dp)
                    .weight(3f),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = file.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Text(
                        text = formatDateTime(file.modified),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )

                    if (!file.is_dir) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = FileUtils.conversionFileSize(file.size),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(.2f))

            if (file.is_dir) {
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = Icons.Default.ChevronRight, contentDescription = null
                )
            }
        }
    }

    //下拉菜单
    Box(
        modifier = Modifier.offset {
            IntOffset(
                animatedOffset.value.x.roundToInt(),
                -animatedOffset.value.y.roundToInt() / 2
            )
        }) {
        DropdownMenu(
            expanded = dropDownState,
            onDismissRequest = { dropDownState = false }
        ) {
            DropPopItem(text = "重命名", onClick = {
                fileRenameClick(file)
                dropDownState = false
            }) {
                Icon(
                    imageVector = Icons.Outlined.DriveFileRenameOutline,
                    contentDescription = null
                )
            }
            DropPopItem(text = "复制", onClick = {
                fileCopyClick(file)
                dropDownState = false
            }) {
                Icon(
                    imageVector = Icons.Outlined.FileCopy,
                    contentDescription = null
                )
            }
            DropPopItem(text = "删除", onClick = {
                fileDeleteClick(file)
                dropDownState = false
            }) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            }
            if (!file.is_dir) {
                DropPopItem(text = "下载", onClick = {
                    fileDownloadClick(file)
                    dropDownState = false
                }) {
                    Icon(
                        imageVector = Icons.Outlined.FileDownload,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun PathCard(
    isLast: Boolean,
    pathName: String,
    index: Int,
    onClick: (index: Int) -> Unit
) {
    Card(
        shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .background(if (isLast) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onClick(index) }
                .padding(16.dp, 4.dp)
        ) {
            Text(
                color = if (isLast) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                text = pathName
            )
        }
    }

    if (!isLast) {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            Modifier.padding(horizontal = 6.dp)
        )
    }
}