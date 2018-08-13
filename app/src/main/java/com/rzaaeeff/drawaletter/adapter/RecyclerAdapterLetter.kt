package com.rzaaeeff.drawaletter.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.rzaaeeff.drawaletter.R
import com.rzaaeeff.drawaletter.persistence.Letter
import java.io.ByteArrayInputStream

class RecyclerAdapterLetter(
        private val context: Context,
        private val letters: List<Letter>,
        private val controller: Controller
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

    inner class LetterHolder(view: View) : RecyclerView.ViewHolder(view), View.OnLongClickListener {

        var textView: TextView = view.findViewById(R.id.textView)
        var imageView: ImageView = view.findViewById(R.id.imageView)

        init {
            view.setOnLongClickListener(this)
        }

        fun bind(letter: Letter) {
            textView.text = letter.letter

            val inputStream = ByteArrayInputStream(letter.image)
            imageView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
        }

        override fun onLongClick(v: View?): Boolean {
            val position = adapterPosition

            if (position != RecyclerView.NO_POSITION) {
                controller.onLetterLongClick(position)
                return true
            }

            return false
        }
    }

    interface Controller {
        fun onLetterLongClick(position: Int)
    }
}