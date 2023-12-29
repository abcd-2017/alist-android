package com.android.alist.ui.compose.file

import android.content.res.Configuration
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DensityMedium
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
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
import com.android.alist.ui.compose.common.RowSpacer
import com.android.alist.utils.FileUtils
import com.android.alist.utils.constant.AppConstant
import com.android.alist.utils.constant.ScreenWidthConstant
import com.android.alist.utils.formatDateTime
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.State
import com.android.alist.utils.BackHandler
import kotlinx.coroutines.launch

@Composable
fun FilePage(
    navController: NavHostController,
    onBackPressedDispatcher: OnBackPressedDispatcher,
    fileViewModel: FileViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val fileListState = fileViewModel.fileList.collectAsState()
    LaunchedEffect(Unit) {
        fileViewModel.initParam(navController)
    }
    if (fileViewModel.pathList.size > 0) {
        BackHandler(backDispatcher = onBackPressedDispatcher) {
            fileViewModel.pathList.removeLast()
            fileViewModel.getFileList()
        }
    }

    FilePageContent(
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        fileList = fileListState,
        isRefresh = fileViewModel.isRefresh,
        pathList = fileViewModel.pathList,
        pathCardClick = { index ->
            fileViewModel.pathList.removeRange(index, fileViewModel.pathList.size)
            fileViewModel.getFileList()
        },
        fileRowOnClick = { file ->
            if (file.is_dir) {
                fileViewModel.pathList.add(file.name)
                fileViewModel.getFileList()
            }
        }, onRefresh = {
            fileViewModel.RefreshFileList()
        }
    )
}

@Composable
fun FilePageContent(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    fileList: State<List<File>>,
    isRefresh: MutableState<Boolean>,
    pathList: SnapshotStateList<String>,
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    onRefresh: () -> Unit
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
            pathCardClick = pathCardClick,
            fileRowOnClick = fileRowOnClick,
            onRefresh = onRefresh
        )
    } else {
        PhonePage(
            configuration = configuration,
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            fileList = fileList,
            isRefresh = isRefresh,
            pathList = pathList,
            pathCardClick = pathCardClick,
            fileRowOnClick = fileRowOnClick,
            onRefresh = onRefresh
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
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    onRefresh: () -> Unit
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
            DrawerContent(width)
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
                        title = { Text("") }
                    )
                }
            ) { paddingValues ->
                FileList(
                    modifier = Modifier.padding(paddingValues),
                    fileList = fileList,
                    isRefresh = isRefresh,
                    pathList = pathList,
                    onClick = pathCardClick,
                    fileRowOnClick = fileRowOnClick,
                    onRefresh = onRefresh
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
    pathCardClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    onRefresh: () -> Unit
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
            DrawerContent(width)
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
                        Icon(
                            imageVector = Icons.Default.DensityMedium,
                            contentDescription = null,
                            Modifier.clickable {
                                coroutineScope.launch { drawerState.open() }
                            }
                        )
                    }, actions = {
                        //操作拦图标
                        Icon(imageVector = Icons.Outlined.CloudUpload, contentDescription = null)
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(
                            imageVector = Icons.Outlined.CreateNewFolder,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(24.dp))
                        Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                )
            }
        ) { paddingValues ->
            FileList(
                modifier = Modifier.padding(paddingValues),
                fileList = fileList,
                isRefresh = isRefresh,
                pathList = pathList,
                onClick = pathCardClick,
                fileRowOnClick = fileRowOnClick,
                onRefresh = onRefresh
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
    onClick: (index: Int) -> Unit,
    fileRowOnClick: (file: File) -> Unit,
    onRefresh: () -> Unit
) {
    val fileScrollState = rememberLazyListState()
    val pathScrollState = rememberLazyListState()
    val pullRefreshState =
        rememberPullRefreshState(isRefresh.value, onRefresh = onRefresh)

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
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
                                    index = i,
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
                    FileRow(it, fileRowOnClick = fileRowOnClick)
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
) {
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.size(30.dp)) {

        }
    }
}

@Composable
fun FileRow(
    file: File,
    fileRowOnClick: (file: File) -> Unit
) {
    Box(modifier = Modifier
        .clickable { fileRowOnClick(file) }) {
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
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = file.name,
                    color = MaterialTheme.colorScheme.onBackground,
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

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .size(30.dp),
                imageVector = Icons.Default.ChevronRight, contentDescription = null
            )
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
                .padding(16.dp, 4.dp)
                .clickable { onClick(index) }
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