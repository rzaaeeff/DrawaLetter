package com.rzaaeeff.drawaletter

import android.app.Application
import android.content.Context
import java.util.*

class DrawaLetter: Application() {
    companion object {
        const val SP_NAME = "com.rzaaeeff.drawaletter.DrawALetter"
        const val SP_UUID = "com.rzaaeeff.drawaletter.DrawALetter.UUID"
    }

    override fun onCreate() {
        super.onCreate()

        val pref = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

        if (!pref.contains(SP_UUID)) {
            pref.edit().putString(SP_UUID, UUID.randomUUID().toString()).apply()
        }
    }
}