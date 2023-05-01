package com.balaabirami.abacusandroid.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

const val ABACUS_DB_NAME = "AbacusDatabase"

@Database(entities = [OrderLog::class, PendingOrder::class], version = 3)
@TypeConverters(OrderConvertor::class, LevelConvertors::class)
abstract class AbacusDatabase : RoomDatabase() {
    companion object {
        private var abacusDatabase: AbacusDatabase? = null

        @Synchronized
        fun getAbacusDatabase(context: Context): AbacusDatabase? {
            if (abacusDatabase == null) {
                abacusDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    AbacusDatabase::class.java, ABACUS_DB_NAME
                ).allowMainThreadQueries().build()
            }
            return abacusDatabase
        }
    }

    abstract fun orderDao(): OrderDao

    abstract fun pendingOrderDao(): PendingOrderDao
}