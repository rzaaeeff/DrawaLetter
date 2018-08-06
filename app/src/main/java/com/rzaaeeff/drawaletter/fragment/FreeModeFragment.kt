package com.rzaaeeff.drawaletter.fragment

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.Letter
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import kotlinx.android.synthetic.main.fragment_free_mode.*
import java.io.ByteArrayOutputStream

class FreeModeFragment : Fragment() {

    // Other elements
    private lateinit var rootLayout: ViewGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootLayout = inflater.inflate(R.layout.fragment_free_mode, container, false) as ViewGroup

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
                    .setTitle(R.string.confirm)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.yes) { dialog, which ->
                        drawingView.clear()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.no) { dialog, which ->
                        // Do nothing
                        dialog.dismiss()
                    }
                    .create().show()
        }

        btnSave.setOnClickListener {
            if (etLetter.text.isEmpty()) {
                etLetter.error = getString(R.string.err_mandatory_field)
                return@setOnClickListener
            }

            val bitmap = drawingView.getBitmap()
            val emptyBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

            if (bitmap.sameAs(emptyBitmap)) {
                Toast.makeText(context, getString(R.string.err_you_did_not_draw_anything), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val byteArray = bos.toByteArray()

            val letter = Letter(
                    letter = etLetter.text.toString(),
                    image = byteArray,
                    isDrawnInFreeMode = true
            )

            AsyncTask.execute {
                LetterDatabase.getInstance(context!!).letterDao().insertLetter(letter)

                activity?.runOnUiThread {
                    drawingView.clear()
                }
            }
        }
    }
}
