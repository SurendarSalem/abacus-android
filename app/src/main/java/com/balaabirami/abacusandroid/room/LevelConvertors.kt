package com.balaabirami.abacusandroid.room

import androidx.room.TypeConverter
import com.balaabirami.abacusandroid.model.Level
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class LevelConvertors {
    @TypeConverter
    fun fromLevelString(level: Level): String {
        val type = object : TypeToken<Level>() {}.type
        return Gson().toJson(level, type)
    }

    @TypeConverter
    fun toLevel(dataString: String?): Level {
        if (dataString == null || dataString.isEmpty()) {
            return Level()
        }
        val type: Type = object : TypeToken<Level>() {}.type
        return Gson().fromJson(dataString, type)
    }
}