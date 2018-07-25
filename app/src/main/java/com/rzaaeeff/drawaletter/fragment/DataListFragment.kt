package com.rzaaeeff.drawaletter.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.Letter
import com.rzaaeeff.drawaletter.persistence.LetterDatabase


class DataListFragment: Fragment() {

    // Other elements
    private val letters = ArrayList<Letter>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recyclerView = RecyclerView(context)
        recyclerView.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        )

        recyclerView.layoutManager = LinearLayoutManager(context!!)
        recyclerView.adapter = RecyclerAdapterLetter(context!!, letters)

        FillDataTask().execute()

        return recyclerView
    }

    inner class FillDataTask: AsyncTask<Void, Void, Void>() {
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
            ): RecyclerView.Adapter<RecyclerAdapterLetter.LetterHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterHolder {
            val view = LayoutInflater.from(context).inflate(
                    R.layout.recycler_item_letter, null
            )

            return LetterHolder(view)
        }

        override fun getItemCount(): Int = letters.size

        override fun onBindViewHolder(holder: LetterHolder, position: Int) {
            holder.bind(letters[position])
        }

        inner class LetterHolder(view: View): RecyclerView.ViewHolder(view) {
            var textView: TextView = view.findViewById(R.id.textView)
            var imageView: ImageView = view.findViewById(R.id.imageView)

            fun bind(letter: Letter) {
                textView.text = letter.letter


            }
        }
    }
}