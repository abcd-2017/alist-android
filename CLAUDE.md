# CLAUDE.md

## 项目概述

alist-android 是 [alist](https://github.com/alist-org/alist) 文件列表程序的 Android 客户端，使用 Kotlin + Jetpack Compose 构建。支持多服务器管理、文件浏览与管理、图片预览、视频播放等功能。

- **包名**: `com.android.alist`
- **最低 SDK**: 29 (Android 10)
- **目标 SDK**: 34 (Android 14)
- **版本**: 1.1.0 (versionCode 2)
- **Kotlin**: 1.9.0, Compose Compiler: 1.5.1

## 构建与运行

```bash
# 构建 debug APK
./gradlew assembleDebug

# 运行测试
./gradlew test

# 运行 Android 仪器化测试
./gradlew connectedAndroidTest
```

项目使用阿里云 Maven 镜像加速依赖下载（见 `settings.gradle`）。

## 架构概览

采用 MVVM 架构，层次分明：

```
UI (Compose) → ViewModel → Repository/DAO → Network (Retrofit) / Database (Room)
```

### 导航路由

通过 Navigation Compose 管理，路由定义在 `PageConstant.kt`：

| 路由 | 页面 | 说明 |
| --- | --- | --- |
| `ServicePage/{changeService}` | ServicePage | 服务器选择与管理 |
| `FilePage` | FilePage | 文件浏览与操作 |
| `ImagePage/{ImagePath}` | ImagePage | 图片预览 |
| `VideoPage/{VideoName}` | VideoPage | 视频播放 |

### 网络层

- `RetrofitClient` — 动态 baseUrl，从 Room 数据库读取当前连接的服务器地址
- `FsApi` — 文件系统 API（/api/fs/*），涵盖增删改查、搜索、移动、复制等
- `UserApi` — 用户认证 API（/api/auth/*, /api/me）
- `RequestInterceptor` — OkHttp 拦截器，通过 `@RequireAuthorization` 注解自动注入 Authorization header，并处理 401 过期跳转

### 数据库层

- `AppDatabase` — Room 数据库单例，数据库名 `alist_db.db`
- `ServiceDao` — 服务器连接信息 CRUD
- `Service` — 服务器实体（ip, port, username, password, description, lastConnected）

### 全局回调

`App.kt` 中定义了两个全局回调，由 `MainActivity` 初始化：

- `globalRequestBeforeCallback` — 请求前 Token 为空时触发，跳转到登录页
- `globalRequestAfterCallback` — 请求返回 401 时触发，清除 Token 并跳转登录页

## 核心页面

### ServicePage（服务器管理）
- 展示已保存的服务器列表（Card 形式）
- 悬浮按钮展开表单添加服务器（IP、端口、用户名、密码、描述）
- 长按卡片弹出菜单：删除、修改
- 点击卡片连接服务器，成功后跳转 FilePage
- 自动连接上次使用的服务器

### FilePage（文件浏览）
- 面包屑路径导航
- 文件/文件夹列表（图标、名称、修改时间、大小）
- 长按文件弹出操作菜单：重命名、复制、删除、下载（文件）
- TopAppBar 操作：上传文件、新建文件夹
- 点击文件夹进入子目录，点击图片跳转 ImagePage，点击视频跳转 VideoPage
- 侧边栏：切换服务器
- 自适应布局：手机用 ModalNavigationDrawer，平板用 Sidebar 布局

### ImagePage（图片预览）
- 显示指定路径下的图片

### VideoPage（视频播放）
- 使用 Media3 ExoPlayer 播放在线视频

## 代码约定

- **语言**: 所有注释和文档使用中文
- **依赖注入**: 使用 Hilt，ViewModel 通过 `@HiltViewModel` 和 `hiltViewModel()` 获取
- **异步**: 网络请求和数据库操作在 `Dispatchers.IO` 协程中执行，UI 更新切回主线程
- **状态管理**: 使用 `MutableState` / `mutableStateOf` 管理 UI 状态，`collectAsState()` 收集 Flow
- **权限**: 使用 Accompanist Permissions 处理存储和相机权限
- **组件复用**: 通用弹窗组件（`AlistDialog`, `AlistAlertDialog`）和下拉菜单项（`DropPopItem`）抽取在 `ui/compose/common/` 中
- **SharedPreferences**: 通过 `SharePreferenceUtils` 工具类存取 Token 和默认服务器地址

## 注意事项

- RetrofitClient 的 baseUrl 从数据库动态读取，**必须**在连接服务器后才能调用 API
- 请求无需 `@RequireAuthorization` 注解时不会注入 Token，public 接口（如登录）不应加此注解
- `AppDatabase.getInstance()` 的单例实现存在并发安全问题（`instance` 始终为 null），但当前使用场景下不影响功能
- 项目同时依赖了 Coil 和 Glide 两个图片加载库
- 使用了部分 alpha 版本的依赖（Room 2.6.0-alpha01, Navigation 2.6.0-alpha08 等）