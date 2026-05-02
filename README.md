# alist-android

alist-android 是 [alist](https://github.com/alist-org/alist) 的 Android 客户端，基于 Jetpack Compose 构建，提供文件浏览、管理、图片预览和视频播放等功能。

## 功能特性

- **多服务器管理** — 支持添加、修改、删除多个 alist 服务器，自动记住最近连接的服务器
- **用户认证** — 支持 alist 的用户名/密码登录，JWT Token 自动管理与过期检测
- **文件浏览** — 目录导航、面包屑路径、文件列表展示（名称、修改时间、大小）
- **文件操作** — 新建文件夹、重命名、删除、下载、上传文件
- **图片预览** — 支持 SVG 图标显示，支持图片无损缩放查看
- **视频播放** — 基于 Media3 ExoPlayer 的在线视频播放
- **文件搜索** — 支持搜索文件和文件夹
- **自适应布局** — 适配手机和平板两种布局模式
- **下拉刷新** — 支持下拉刷新文件列表

## 技术栈

| 类别 | 技术 |
| --- | --- |
| UI 框架 | Jetpack Compose + Material 3 |
| 依赖注入 | Hilt |
| 网络请求 | Retrofit + OkHttp + Gson |
| 本地数据库 | Room |
| 图片加载 | Coil（支持 SVG） |
| 视频播放 | Media3 ExoPlayer |
| 导航 | Navigation Compose |
| 权限管理 | Accompanist Permissions |
| 本地存储 | DataStore Preferences |
| 最低 SDK | Android 10 (API 29) |
| 目标 SDK | Android 14 (API 34) |

## 项目结构

```
app/src/main/java/com/android/alist/
├── App.kt                        # Application 入口，全局变量
├── database/                     # 数据库层
│   ├── AppDatabase.kt            # Room 数据库实例
│   ├── dao/ServiceDao.kt         # 服务器信息 DAO
│   └── table/Service.kt          # 服务器信息实体
├── network/                      # 网络层
│   ├── RetrofitClient.kt         # Retrofit 客户端（动态 baseUrl）
│   ├── annotation/               # 自定义注解（RequireAuthorization）
│   ├── api/FsApi.kt              # 文件系统 API 接口
│   ├── api/UserApi.kt            # 用户认证 API 接口
│   ├── entity/                   # 响应实体类
│   ├── interceptor/              # OkHttp 拦截器（Token 注入/过期处理）
│   └── to/                       # 请求体 DTO
├── state/                        # UI 状态类
├── ui/
│   ├── activity/MainActivity.kt  # 主 Activity，导航图
│   ├── compose/
│   │   ├── common/               # 通用组件（Dialog、下拉菜单等）
│   │   ├── file/                 # 文件浏览页面
│   │   ├── image/                # 图片预览页面
│   │   ├── service/              # 服务器选择与管理页面
│   │   └── video/                # 视频播放页面
│   └── theme/                    # Material 3 主题配置
└── utils/                        # 工具类（文件、网络、权限、日期等）
```

## 构建运行

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK 34
- Gradle 8.x

### 构建步骤

1. 克隆项目：
   ```bash
   git clone https://github.com/abcd-2017/alist-android.git
   ```
2. 使用 Android Studio 打开项目，等待 Gradle 同步完成
3. 选择设备或模拟器（API 29+），点击运行

### 配置 alist 服务端

使用前需要先部署 alist 服务端，参考 [alist 官方文档](https://alist.nn.ci/zh/guide/install/)。

## 使用说明

1. 启动应用后进入服务器管理页面
2. 点击右下角 **+** 按钮添加 alist 服务器（IP、端口、用户名、密码）
3. 点击服务器卡片进行连接
4. 登录成功后进入文件浏览页面，可进行文件管理操作
5. 侧边栏菜单可切换服务器

## License

MIT License
