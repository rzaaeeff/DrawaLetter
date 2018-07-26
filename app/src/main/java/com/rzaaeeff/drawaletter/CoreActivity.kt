package com.rzaaeeff.drawaletter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.rzaaeeff.drawaletter.fragment.DataListFragment
import com.rzaaeeff.drawaletter.fragment.DrawFragment
import com.rzaaeeff.drawaletter.fragment.MainFragment
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import android.os.StrictMode
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_core)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MainFragment())
                .commit()
    }

    fun openDrawFragment(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DrawFragment())
                .addToBackStack(null)
                .commit()
    }

    fun openListFragment(v: View) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DataListFragment())
                .addToBackStack(null)
                .commit()
    }

    fun share(v: View) {
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            exportDatabse()
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
                    exportDatabse()
                }
            }
        }
    }

    private fun exportDatabse() {
        var backupDB: File? = null
        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()

            if (sd.canWrite()) {
                val currentDBPath = ("//data//" + packageName
                        + "//databases//" + LetterDatabase.DB_NAME + "")
                val currentDB = File(data, currentDBPath)
                backupDB = File(sd, LetterDatabase.DB_NAME)

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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

//        val emailIntent = Intent(android.content.Intent.ACTION_SEND)
//        emailIntent.type = "*/*"
//        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
//                arrayOf("rzaaeeff@gmail.com"))
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//                "[Handwritten Letter Data-set]")
//
//        val r = Random()
//
//        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
//                "Local db " + r.nextInt())
//        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupDB))
//        startActivity(Intent.createChooser(emailIntent, "Export database"))

        val intentShareFile = Intent(Intent.ACTION_SEND)
        intentShareFile.setType("*/*");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${backupDB?.canonicalPath}"))

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                "[Draw A Letter | Data-set]");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }
}
