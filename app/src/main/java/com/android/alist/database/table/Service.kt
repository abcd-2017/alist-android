package com.android.alist.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * alist 服务器信息
 */
@Entity(tableName = "service")
data class Service(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    var id: Int?,
    //服务器ip
    @ColumnInfo(name = "ip", typeAffinity = ColumnInfo.TEXT)
    var ip: String,
    //服务器端口
    @ColumnInfo(name = "port", typeAffinity = ColumnInfo.INTEGER)
    var port: Int,
    //是否为默认服务器
    @ColumnInfo(name = "isDefault", typeAffinity = ColumnInfo.INTEGER)
    var isDefault: Int,
    //服务器描述
    @ColumnInfo(name = "description", typeAffinity = ColumnInfo.TEXT)
    var description: String?,
    //保存的用户名
    @ColumnInfo(name = "username", typeAffinity = ColumnInfo.TEXT)
    var username: String,
    //保存的密码
    @ColumnInfo(name = "password", typeAffinity = ColumnInfo.TEXT)
    var password: String,
    @ColumnInfo(name = "updateDate", typeAffinity = ColumnInfo.TEXT)
    var updateDate: String
)