package com.balaabirami.abacusandroid.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingOrderDao {

    @Query("SELECT * FROM pending_order")
    fun getOrderLogs(): List<PendingOrder>

    @Insert
    fun insert(pendingOrder: PendingOrder)

    @Query("DELETE from pending_order WHERE order_id=:orderId AND student_id=:studentId")
    fun delete(studentId: String, orderId: String)

    @Query("SELECT * FROM pending_order WHERE student_id=:studentId")
    fun getPendingOrder(studentId: String): List<PendingOrder>
}