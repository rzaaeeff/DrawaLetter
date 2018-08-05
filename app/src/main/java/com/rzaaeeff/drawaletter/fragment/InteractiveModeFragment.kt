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
import kotlinx.android.synthetic.main.fragment_interactive_mode.*
import java.io.ByteArrayOutputStream
import java.util.*

class InteractiveModeFragment : Fragment() {
    // Constants

    // Widgets

    // Other elements
    private val alphabet = mutableListOf<Char>()
    private var index = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_interactive_mode, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initValues()
        setListeners()
    }

    private fun initValues() {
        AsyncTask.execute {
            constructAlphabet()

            index = LetterDatabase.getInstance(context!!).letterDao().count()

            activity?.runOnUiThread {
                drawingView.isDrawingCacheEnabled = true
                drawingView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

                showNextLetter()
            }
        }
    }

    private fun setListeners() {
        btnNext.setOnClickListener {
            if (!saveLetter()) {
                Toast.makeText(context, getString(R.string.err_you_did_not_draw_anything), Toast.LENGTH_SHORT).show()
            } else if (!showNextLetter()) {
                Toast.makeText(context, getString(R.string.you_have_completed_task), Toast.LENGTH_LONG).show()
                activity?.onBackPressed()
            }
        }

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
    }

    private fun constructAlphabet() {
        val base = getString(R.string.alphabet).toMutableList()
        val capitals = mutableListOf<Char>()

        for (letter in base)
            capitals.add(letter.toString().toUpperCase(Locale("az")).toCharArray()[0])

        alphabet.addAll(base)
        alphabet.addAll(capitals)

        index = 0
    }

    private fun showNextLetter(): Boolean {
        val letter = alphabet[index % alphabet.size]


        tvLetter.text = String.format(
                getString(R.string.instruction_format),
                letter,
                getString(if (letter.isUpperCase()) R.string.uppercase else R.string.lowercase),
                getString(if (letter.isUpperCase()) R.string.uppercase_en else R.string.lowercase_en)
        )

        return index++ % alphabet.size != 0
    }

    private fun saveLetter(): Boolean {
        drawingView.invalidate()
        val bitmap = drawingView.getBitmap()
        val emptyBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        if (bitmap.sameAs(emptyBitmap)) {
            return false
        }

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val byteArray = bos.toByteArray()

        val letter = Letter(
                letter = alphabet[(index - 1) % alphabet.size].toString(),
                image = byteArray
        )

        AsyncTask.execute {
            LetterDatabase.getInstance(context!!).letterDao().insertLetter(letter)

            activity?.runOnUiThread {
                drawingView?.clear()
            }
        }

        return true
    }
}