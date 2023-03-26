package com.balaabirami.abacusandroid.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_logs")
data class OrderLog(
    @ColumnInfo(name = "order_id") val orderId: String,
    @ColumnInfo(name = "step") val step: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
        get() = field
        set(value) = Unit
}