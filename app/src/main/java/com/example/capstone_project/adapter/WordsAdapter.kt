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
    private var words: List<Word>,
    private val onFavoriteClick: (Word, View) -> Unit,
    private val onWordClick: (Word, View) -> Unit
) : ArrayAdapter<Word>(context, 0, words) {

    private var showHitsOnly: Boolean = false
    private var showMissesOnly: Boolean = false

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

        if (showHitsOnly) {
            binding.tvHits.visibility = View.VISIBLE
            binding.tvHitsDesc.visibility = View.VISIBLE
            binding.tvMisses.visibility = View.GONE
            binding.tvMissesDesc.visibility = View.GONE
        } else if (showMissesOnly) {
            binding.tvHits.visibility = View.GONE
            binding.tvHitsDesc.visibility = View.GONE
            binding.tvMisses.visibility = View.VISIBLE
            binding.tvMissesDesc.visibility = View.VISIBLE
        } else {
            binding.tvHits.visibility = View.VISIBLE
            binding.tvHitsDesc.visibility = View.VISIBLE
            binding.tvMisses.visibility = View.VISIBLE
            binding.tvMissesDesc.visibility = View.VISIBLE
        }

        binding.btnFavorite.setImageResource(if (word?.isFavorite == true) R.drawable.ic_star_24_favourited else R.drawable.ic_star_24_gray)

        binding.btnFavorite.setOnClickListener {
            word?.let { onFavoriteClick(it, binding.root) }
        }

        binding.root.setOnClickListener {
            word?.let { onWordClick(it, binding.root) }
        }

        return binding.root
    }

    fun updateWords(newWords: List<Word>) {
        words = newWords
        notifyDataSetChanged()
    }

    fun showHitsOnly() {
        showHitsOnly = true
        showMissesOnly = false
        notifyDataSetChanged()
    }

    fun showMissesOnly() {
        showHitsOnly = false
        showMissesOnly = true
        notifyDataSetChanged()
    }

    fun showAll() {
        showHitsOnly = false
        showMissesOnly = false
        notifyDataSetChanged()
    }
}
