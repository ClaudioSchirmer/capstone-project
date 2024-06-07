package com.example.capstone_project.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.capstone_project.R

class WordAdapter(context: Context, words: List<String>) :
    ArrayAdapter<String>(context, 0, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val wordText = listItemView!!.findViewById<TextView>(R.id.word_text)
        val searchText = listItemView.findViewById<TextView>(R.id.search_text)
        val word = getItem(position)

        wordText.text = word
        searchText.setOnClickListener {
            val url = "https://www.google.com/search?q=${Uri.encode(word)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }

        return listItemView
    }

}