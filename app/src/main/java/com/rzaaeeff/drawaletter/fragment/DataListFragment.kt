package com.rzaaeeff.drawaletter.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.Letter
import com.rzaaeeff.drawaletter.persistence.LetterDatabase
import kotlinx.android.synthetic.main.fragment_interactive_mode.*
import java.io.ByteArrayInputStream


class DataListFragment : Fragment() {

    // Other elements
    private val letters = ArrayList<Letter>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recyclerView = RecyclerView(context)
        recyclerView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        )

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = RecyclerAdapterLetter(context!!, letters)

        FillDataTask().execute()

        setHasOptionsMenu(true)

        return recyclerView
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.itemClear -> {
                AlertDialog.Builder(activity!!)
                        .setTitle(R.string.confirm)
                        .setMessage(R.string.are_you_sure)
                        .setPositiveButton(R.string.yes) { dialog, which ->
                            AsyncTask.execute {
                                LetterDatabase.getInstance(context!!).letterDao().deleteAllLetters()

                                activity!!.runOnUiThread {
                                    dialog.dismiss()
                                    activity!!.onBackPressed()
                                }
                            }
                        }
                        .setNegativeButton(R.string.no) { dialog, which ->
                            // Do nothing
                            dialog.dismiss()
                        }
                        .create().show()

                return true
            }

            else -> return false
        }
    }

    inner class FillDataTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            letters.addAll(LetterDatabase.getInstance(context!!).letterDao().getAllLetters())

            return null
        }

        override fun onPostExecute(result: Void?) {
            recyclerView.adapter.notifyDataSetChanged()
        }
    }

    inner class RecyclerAdapterLetter(
            private val context: Context,
            private val letters: List<Letter>
    ) : RecyclerView.Adapter<RecyclerAdapterLetter.LetterHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val view = LayoutInflater.from(context).inflate(
                    R.layout.recycler_item_letter, parent, false
            )

            return LetterHolder(view)
        }

        override fun getItemCount(): Int = letters.size

        override fun onBindViewHolder(holder: LetterHolder, position: Int) {
            holder.bind(letters[position])
        }

        inner class LetterHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView: TextView = view.findViewById(R.id.textView)
            var imageView: ImageView = view.findViewById(R.id.imageView)

            fun bind(letter: Letter) {
                textView.text = letter.letter

                val inputStream = ByteArrayInputStream(letter.image)
                imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
            }
        }
    }
}