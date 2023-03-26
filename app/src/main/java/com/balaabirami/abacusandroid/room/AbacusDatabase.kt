package com.balaabirami.abacusandroid.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

const val ABACUS_DB_NAME = "AbacusDatabase"

@Database(entities = arrayOf(OrderLog::class), version = 1)
abstract class AbacusDatabase : RoomDatabase() {
    companion object {
        private var abacusDatabase: AbacusDatabase? = null

        @Synchronized
        fun getAbacusDatabase(context: Context): AbacusDatabase? {
            if (abacusDatabase == null) {
                abacusDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    AbacusDatabase::class.java, ABACUS_DB_NAME
                ).build()
            }
            return abacusDatabase
        }
    }

    abstract fun orderDao(): OrderDao
}