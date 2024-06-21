package com.example.capstone_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.capstone_project.R
import com.example.capstone_project.infrastructure.data.entities.Word

class WordsAdapter(
    private val context: Context,
    private val words: List<Word>,
    private val onFavoriteClick: (Word, View) -> Unit,
    private val onWordClick: (Word, View) -> Unit
) : ArrayAdapter<Word>(context, 0, words) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val word = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_word, parent, false)
        val wordTextView: TextView = view.findViewById(R.id.tv_word)
        val favoriteImageView: ImageView = view.findViewById(R.id.btn_favorite)

        wordTextView.text = word?.word
        favoriteImageView.setImageResource(if (word?.isFavorite == true) R.drawable.ic_star_24_favourited else R.drawable.ic_star_24_gray)

        favoriteImageView.setOnClickListener {
            word?.let { onFavoriteClick(it, view) }
        }

        view.setOnClickListener {
            word?.let { onWordClick(it, view) }
        }

        return view
    }
}





