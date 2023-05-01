package com.balaabirami.abacusandroid.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.balaabirami.abacusandroid.model.Level
import com.balaabirami.abacusandroid.model.Order

@Entity(tableName = "pending_order")
data class PendingOrder(
    @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "student_id") val studentId: String,
    @TypeConverters(OrderConvertor::class, LevelConvertors::class)
    @ColumnInfo(name = "order") val order: Order
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
        get() = field
        set(value) = Unit
}