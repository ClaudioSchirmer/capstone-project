package com.example.capstone_project.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.adapter.WordsAdapter
import com.example.capstone_project.databinding.FragmentWordsBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment() {
    private lateinit var adapter: WordsAdapter
    private lateinit var words: MutableList<Word>
    private lateinit var binding: FragmentWordsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsBinding.inflate(inflater, container, false)
        words = mutableListOf()
        adapter = WordsAdapter(requireContext(), words, { word, _ ->
            toggleFavorite(word)
        }, { _, _ ->
        })

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                words.addAll(AppDatabase.getDatabase(requireContext()).wordDAO().getFavorites())
            }
            adapter.notifyDataSetChanged()
        }

        binding.list.adapter = adapter

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (words.isNotEmpty()) {
                    binding.textViewListEmpty.visibility = View.GONE
                } else {
                    binding.textViewListEmpty.visibility = View.VISIBLE
                }
            }
        }

        return binding.root
    }

    private fun toggleFavorite(word: Word) {
        word.isFavorite = !word.isFavorite
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).wordDAO().updateFavoriteStatus(word)
        }
        adapter.notifyDataSetChanged()
    }
}


