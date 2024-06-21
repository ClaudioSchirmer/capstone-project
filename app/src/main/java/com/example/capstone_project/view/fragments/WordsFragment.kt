package com.example.capstone_project.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.adapter.WordsAdapter
import com.example.capstone_project.databinding.FragmentWordsBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordsFragment : Fragment() {
    private lateinit var adapter: WordsAdapter
    private lateinit var words: MutableList<Word>
    private lateinit var binding: FragmentWordsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsBinding.inflate(inflater, container, false)
        words = mutableListOf()
        adapter = WordsAdapter(requireContext(), words, { word, view ->
            toggleFavorite(word)
        }, { word, view ->
            showContextMenu(word, view)
        })

        binding.list.adapter = adapter

        loadWordsFromDatabase()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (words.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        binding.textViewListEmpty.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.textViewListEmpty.visibility = View.VISIBLE
                    }
                }
            }
        }

        registerForContextMenu(binding.list)
        return binding.root
    }

    private fun loadWordsFromDatabase() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val loadedWords = AppDatabase.getDatabase(requireContext()).wordDAO().getAll()
                withContext(Dispatchers.Main) {
                    words.clear()
                    loadedWords.forEach { word ->
                        if (!words.any { it.word == word.word }) {
                            words.add(word)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun toggleFavorite(word: Word) {
        word.isFavorite = !word.isFavorite
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).wordDAO().updateFavoriteStatus(word)
        }
        adapter.notifyDataSetChanged()
    }

    fun addWordToList(wordText: String) {
        val existingWord = words.find { it.word == wordText }
        if (existingWord != null) {
            return
        }

        val newWord = Word(word = wordText, definition = "", isFavorite = false)
        words.add(newWord)
        adapter.notifyDataSetChanged()

        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).wordDAO().insert(newWord)
        }
    }

    private fun showContextMenu(word: Word, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.menu.add(0, 0, 0, "Search Synonyms")
        menu.menu.add(0, 2, 1, "Search Definitions")
        menu.setOnMenuItemClickListener { item ->
            when (item.order) {
                0 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=synonyms+of+${word.word}"))
                    startActivity(intent)
                    true
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=definition+of+${word.word}"))
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
}
