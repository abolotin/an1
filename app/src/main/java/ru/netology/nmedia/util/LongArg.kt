package ru.netology.nmedia.util

import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object LongArg : ReadWriteProperty<Bundle, Long?> {
    override fun getValue(thisRef: Bundle, property: KProperty<*>) = thisRef.getLong(property.name, 0)

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: Long?) {
        thisRef.putLong(property.name, value ?: 0L)
    }
}