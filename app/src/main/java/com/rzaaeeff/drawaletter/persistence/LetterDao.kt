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
    @Query("SELECT * FROM Letters")
    fun getAllLetters(): List<Letter>

    /**
     * Insert new letter.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLetter(letter: Letter)

    /**
     * Delete all letters.
     */
    @Query("DELETE FROM Letters")
    fun deleteAllLetters()
}