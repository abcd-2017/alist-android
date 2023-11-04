package com.android.alist.database

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.alist.database.dao.ServiceDao
import com.android.alist.database.table.Service

@Database(entities = [Service::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @SuppressLint("staticfieldleak")
        private val instance: AppDatabase? = null

        private val DATABASE_NAME = "alist_db.db"

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()
            }
        }
    }

    //访问数据库对象
    abstract fun getServiceDao(): ServiceDao
}