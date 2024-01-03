package com.android.alist.network.api

import com.android.alist.network.annotation.RequireAuthorization
import com.android.alist.network.entity.fs.FileListEntity
import com.android.alist.network.entity.ResponseData
import com.android.alist.network.entity.fs.DirsEntity
import com.android.alist.network.entity.fs.FileInfoEntity
import com.android.alist.network.entity.fs.SearchFileEntity
import com.android.alist.network.to.fs.FsBatchRenameTo
import com.android.alist.network.to.fs.FsFileListTo
import com.android.alist.network.to.fs.FsFileOrDirTo
import com.android.alist.network.to.fs.FsFormFIleTo
import com.android.alist.network.to.fs.FsMkdirTo
import com.android.alist.network.to.fs.FsMoveFileTo
import com.android.alist.network.to.fs.FsRecursiveMoveFileTo
import com.android.alist.network.to.fs.FsRemoveEmptyDirectoryTo
import com.android.alist.network.to.fs.FsRemoveFileTo
import com.android.alist.network.to.fs.FsRenameTo
import com.android.alist.network.to.fs.FsSearchFileTo
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * fs Api请求接口
 */
interface FsApi {
    /**
     * 新建文件夹
     */
    @POST("/api/fs/mkdir")
    @RequireAuthorization
    suspend fun mkdirFile(
        @Body mkdirTo: FsMkdirTo
    ): ResponseData<Unit>

    /**
     * 重命名文件
     */
    @POST("/api/fs/rename")
    @RequireAuthorization
    suspend fun renameFile(
        @Body renameTo: FsRenameTo
    ): ResponseData<Unit>

    /**
     * 表单上传文件
     */
    @POST("/api/fs/form")
    @RequireAuthorization
    suspend fun formFile(
        @Body formFIleTo: FsFormFIleTo
    ): ResponseData<Unit>

    /**
     * 列出文件目录
     */
    @POST("/api/fs/list")
    @RequireAuthorization
    suspend fun fileList(
        @Body fileListTo: FsFileListTo
    ): ResponseData<FileListEntity>


    /**
     * 获取某个文件/目录信息
     */
    @POST("/api/fs/get")
    @RequireAuthorization
    suspend fun getFileOrDir(
        @Body fileOrDirTo: FsFileOrDirTo
    ): ResponseData<FileInfoEntity>


    /**
     * 搜索文件或文件夹
     */
    @POST("/api/fs/search")
    @RequireAuthorization
    suspend fun searchFile(
        @Body searchFileTo: FsSearchFileTo
    ): ResponseData<SearchFileEntity>

    /**
     * 获取目录
     */
    @POST("/api/fs/dirs")
    @RequireAuthorization
    suspend fun searchFile(
        @Body fileOrDirTo: FsFileOrDirTo
    ): ResponseData<DirsEntity>

    /**
     * 批量重命名
     */
    @POST("/api/fs/batch_rename")
    @RequireAuthorization
    suspend fun batchRename(
        @Body batchRenameTo: FsBatchRenameTo
    ): ResponseData<Unit>

    /**
     * 正则重命名
     */
    @POST("/api/fs/regex_rename")
    @RequireAuthorization
    suspend fun regexRename(
        @Body batchRenameTo: FsBatchRenameTo
    ): ResponseData<Unit>

    /**
     * 移动文件
     */
    @POST("/api/fs/move")
    @RequireAuthorization
    suspend fun moveFile(
        @Body moveFileTo: FsMoveFileTo
    ): ResponseData<Unit>

    /**
     * 聚合移动
     */
    @POST("/api/fs/recursive_move")
    @RequireAuthorization
    suspend fun recursiveMoveFile(
        @Body recursiveMoveFileTo: FsRecursiveMoveFileTo
    ): ResponseData<Unit>

    /**
     * 复制文件
     */
    @POST("/api/fs/copy")
    @RequireAuthorization
    suspend fun copyFile(
        @Body moveFileTo: FsMoveFileTo
    ): ResponseData<Unit>

    /**
     * 删除文件或文件夹
     */
    @POST("/api/fs/remove")
    @RequireAuthorization
    suspend fun removeFile(
        @Body removeFileTo: FsRemoveFileTo
    ): ResponseData<Unit>

    /**
     * 删除空文件夹
     */
    @POST("/api/fs/remove_empty_directory")
    @RequireAuthorization
    suspend fun removeEmptyDirectory(
        @Body removeEmptyDirectoryTo: FsRemoveEmptyDirectoryTo
    ): ResponseData<Unit>

    /**
     * 流式上传文件
     */
    @POST("/api/fs/put")
    @RequireAuthorization
    suspend fun putUploadFile(
        @Body body: String
    ): ResponseData<Unit>

    /**
     * 下载文件
     */
    @GET("/d/{path}")
    suspend fun downloadFile(
        @Path("path") path: String
    ): ResponseBody
}