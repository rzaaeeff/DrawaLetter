package com.rzaaeeff.drawaletter.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "letters")
data class Letter(
        @PrimaryKey(autoGenerate = true)
        val id: Int? = null,

        @ColumnInfo()
        val letter: String,

        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val image: ByteArray,

        @ColumnInfo()
        val isDrawnInFreeMode: Boolean = false
)