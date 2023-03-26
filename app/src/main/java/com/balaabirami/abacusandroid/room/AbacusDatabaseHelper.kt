package com.balaabirami.abacusandroid.room

import android.content.Context

class AbacusDatabaseHelper {

    fun getDao(context: Context): OrderDao? {
        val database = AbacusDatabase.getAbacusDatabase(context)
        return database?.orderDao()
    }
}