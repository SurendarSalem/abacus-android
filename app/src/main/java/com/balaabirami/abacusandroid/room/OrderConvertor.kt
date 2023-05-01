package com.balaabirami.abacusandroid.room

import androidx.room.TypeConverter
import com.balaabirami.abacusandroid.model.Level
import com.balaabirami.abacusandroid.model.Order
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class OrderConvertor {

    @TypeConverter
    fun fromListToString(order: Order): String {
        val type = object : TypeToken<Order>() {}.type
        return Gson().toJson(order, type)
    }

    @TypeConverter
    fun toData(dataString: String?): Order {
        if (dataString == null || dataString.isEmpty()) {
            return Order()
        }
        val type: Type = object : TypeToken<Order>() {}.type
        return Gson().fromJson(dataString, type)
    }
}