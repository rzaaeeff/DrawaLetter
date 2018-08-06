package com.rzaaeeff.drawaletter.fragment

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_mode, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemWidth -> {
                val imageList = listOf(
                        R.drawable.width_10px_drawable,
                        R.drawable.width_20px_drawable,
                        R.drawable.width_30px_drawable,
                        R.drawable.width_40px_drawable,
                        R.drawable.width_50px_drawable
                )

                val builder = AlertDialog.Builder(activity!!)
                builder.setAdapter(object : ArrayAdapter<Int>(activity, R.layout.dialog_image, imageList) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
                        val view: View?
                        if (convertView == null) {
                            val inflater = activity!!.layoutInflater
                            view = inflater.inflate(R.layout.dialog_image, parent, false)
                        } else {
                            view = convertView
                        }

                        val imageView = view!!.findViewById(R.id.image) as ImageView
                        val resId = getItem(position)!!
                        imageView.setImageResource(resId)
                        return view
                    }
                }) { dialog, which ->
                    when (which) {
                        0 -> drawingView.setStrokeWidth(10f)
                        1 -> drawingView.setStrokeWidth(20f)
                        2 -> drawingView.setStrokeWidth(30f)
                        3 -> drawingView.setStrokeWidth(40f)
                        4 -> drawingView.setStrokeWidth(50f)
                    }
                }

                builder.create().show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initValues() {
        drawingView.isDrawingCacheEnabled = true
        drawingView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        setHasOptionsMenu(true)
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
