package com.rzaaeeff.drawaletter.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Letter::class], version = 1)
abstract class LetterDatabase: RoomDatabase() {
    abstract fun letterDao(): LetterDao

    companion object {
        @Volatile private var INSTANCE: LetterDatabase? = null

        fun getInstance(context: Context): LetterDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                    LetterDatabase::class.java, "DrawaLetter.db").build()
    }
}