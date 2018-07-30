package com.rzaaeeff.drawaletter.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface LetterDao {
    /**
     * Get all letters.
     */
    @Query("SELECT * FROM letters")
    fun getAllLetters(): List<Letter>

    /**
     * Insert new letter.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLetter(letter: Letter)

    /**
     * Delete all letters.
     */
    @Query("DELETE FROM letters")
    fun deleteAllLetters()

    /**
     * Count of all entries.
     */
    @Query("SELECT COUNT(*) FROM letters")
    fun count() : Int
}