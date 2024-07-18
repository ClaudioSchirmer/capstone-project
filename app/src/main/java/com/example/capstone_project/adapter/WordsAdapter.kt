package com.example.capstone_project.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.capstone_project.R
import com.example.capstone_project.databinding.ListItemWordBinding
import com.example.capstone_project.infrastructure.data.entities.Word

class WordsAdapter(
    private val context: Context,
    private val words: List<Word>,
    private val onFavoriteClick: (Word, View) -> Unit,
    private val onWordClick: (Word, View) -> Unit
) : ArrayAdapter<Word>(context, 0, words) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = if (convertView == null) {
            ListItemWordBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ListItemWordBinding.bind(convertView)
        }

        val word = getItem(position)
        binding.tvWord.text = word?.word
        binding.tvCategory.text = word?.category
        binding.tvHits.text = word?.hits?.toString() ?: "0"
        binding.tvMisses.text = word?.misses?.toString() ?: "0"
        binding.tvHitsDesc.text = if ((word?.hits ?: 0) == 1L) "Hit" else "Hits"
        binding.tvMissesDesc.text = if ((word?.misses ?: 0) == 1L) "Miss" else "Misses"

        binding.btnFavorite.setImageResource(if (word?.isFavorite == true) R.drawable.ic_star_24_favourited else R.drawable.ic_star_24_gray)

        binding.btnFavorite.setOnClickListener {
            word?.let { onFavoriteClick(it, binding.root) }
        }

        binding.root.setOnClickListener {
            word?.let { onWordClick(it, binding.root) }
        }

        return binding.root
    }
}