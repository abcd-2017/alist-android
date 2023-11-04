package com.android.alist.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * alist 服务器信息
 */
@Entity(tableName = "service")
data class Service(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val id: Int,
    @ColumnInfo(name = "ip", typeAffinity = ColumnInfo.TEXT)
    val ip: String,
    @ColumnInfo(name = "port", typeAffinity = ColumnInfo.INTEGER)
    val port: Int,
    @ColumnInfo(name = "isDefault", typeAffinity = ColumnInfo.INTEGER)
    val isDefault: Int
)