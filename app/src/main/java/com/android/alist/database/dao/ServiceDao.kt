package com.android.alist.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.android.alist.database.table.Service

@Dao
interface ServiceDao {
    /**
     * 查询所有alist服务信息
     */
    @Query("select * from service")
    fun queryAll(): List<Service>

    /**
     * 查询默认选择的服务
     */
    @Query("select * from service where isDefault = :isDefault")
    fun queryDefault(isDefault: Int): Service

    /**
     * 修改默认的服务
     */
    @Query(
        "update service set isDefault = case " +
                "when id = :sId then :defaultValue else :notDefaultValue end " +
                "where id = :sId "
    )
    fun updateDefault(sId: Int, defaultValue: Int, notDefaultValue: Int)

    /**
     * 清除默认选中的服务
     */
    @Query("update service set isDefault = :defaultValue where isDefault = :notDefaultValue")
    fun clearDefault(defaultValue: Int, notDefaultValue: Int)

    /**
     * 插入服务
     */
    @Insert
    fun insert(service: Service)

    /**
     * 修改服务
     */
    @Update
    fun update(service: Service)

    /**
     * 删除服务
     */
    @Delete
    fun deleteById(service: Service)
}