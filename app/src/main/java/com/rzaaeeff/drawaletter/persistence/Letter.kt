package com.rzaaeeff.drawaletter.persistence

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "letters")
data class Letter(
        @PrimaryKey
        val id: String = UUID.randomUUID().toString(),

        @ColumnInfo(name = "letter")
        val letter: String,

        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val image: ByteArray
)