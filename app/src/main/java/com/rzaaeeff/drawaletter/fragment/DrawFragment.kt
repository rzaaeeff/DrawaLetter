package com.rzaaeeff.drawaletter.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.Letter
import kotlinx.android.synthetic.main.fragment_draw.*
import android.graphics.Bitmap
import android.os.AsyncTask
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import java.io.ByteArrayOutputStream


class DrawFragment : Fragment() {

    // Other elements
    var rootLayout: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_draw, container, false) as ViewGroup

        return rootLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initValues()
        setListeners()
    }

    private fun initValues() {
        drawingView.isDrawingCacheEnabled = true
        drawingView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    private fun setListeners() {
        btnErase.setOnClickListener {
            AlertDialog.Builder(activity!!)
                    .setTitle("Confirm")
                    .setMessage("Are you sure?")
                    .setPositiveButton("YES") { dialog, which ->
                        drawingView.clear()
                        dialog.dismiss()
                    }
                    .setNegativeButton("NO") { dialog, which ->
                        // Do nothing
                        dialog.dismiss()
                    }
                    .create().show()
        }

        btnSave.setOnClickListener {
            if (etLetter.text.isEmpty()) {
                etLetter.error = "Mandatory field"
                return@setOnClickListener
            }

            val bitmap = drawingView.getBitmap()
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val byteArray = bos.toByteArray()

            val letter = Letter(
                    letter = etLetter.text.toString(),
                    image = byteArray
            )

            AsyncTask.execute {
                LetterDatabase.getInstance(context!!).letterDao().insertLetter(letter)

                activity?.runOnUiThread {
                    drawingView.clear()
                    etLetter.text = null
                }
            }
        }
    }
}
