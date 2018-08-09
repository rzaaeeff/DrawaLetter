package com.rzaaeeff.drawaletter

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rzaaeeff.drawaletter.fragment.DataListFragment
import com.rzaaeeff.drawaletter.fragment.FreeModeFragment
import com.rzaaeeff.drawaletter.fragment.InteractiveModeFragment
import com.rzaaeeff.drawaletter.fragment.MainFragment
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainFragment())
                .commit()
    }

    fun openFreeModeFragment(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, FreeModeFragment())
                .addToBackStack(null)
                .commit()
    }

    fun openListFragment(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DataListFragment())
                .addToBackStack(null)
                .commit()
    }

    fun openInteractiveMode(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, InteractiveModeFragment())
                .addToBackStack(null)
                .commit()
    }

    fun share(v: View) {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            exportAndSendDatabase()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportAndSendDatabase()
                }
            }
        }
    }

    private fun exportAndSendDatabase() {
        var backupDB: File? = null

        val prefs = getSharedPreferences(DrawaLetter.SP_NAME, Context.MODE_PRIVATE)
        val uuid = prefs.getString(DrawaLetter.SP_UUID, "NONE")
        val time = System.currentTimeMillis()

        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()

            if (sd.canWrite()) {
                val currentDBPath = ("//data//" + packageName
                        + "//databases//" + LetterDatabase.DB_NAME)
                val currentDB = File(data, currentDBPath)
                backupDB = File(sd, "$time-$uuid-" + LetterDatabase.DB_NAME)

                if (currentDB.exists()) {

                    val src = FileInputStream(currentDB)
                            .channel
                    val dst = FileOutputStream(backupDB)
                            .channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                }
            }
        } catch (e: Exception) {}

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {}

        }

        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.type = "*/*"
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${backupDB?.canonicalPath}"))

        intentShareFile.putExtra(Intent.EXTRA_EMAIL, arrayOf("rzaaeeff@gmail.com"))
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "[Draw A Letter | Data-set]")
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...\n$uuid")

        startActivity(Intent.createChooser(intentShareFile, "Share File"))
    }
}
