package com.balaabirami.abacusandroid.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Query("SELECT * FROM order_logs")
    fun getOrderLogs(): List<OrderLog>

    @Insert
    fun insert(orderLog: OrderLog)

    @Delete
    fun delete(user: OrderLog)

    @Query("DELETE FROM order_logs")
    fun clear()
}