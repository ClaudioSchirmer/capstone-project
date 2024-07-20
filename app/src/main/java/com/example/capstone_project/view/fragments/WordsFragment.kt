package com.example.capstone_project.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
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
    private lateinit var allWords: MutableList<Word>
    private lateinit var binding: FragmentWordsBinding
    private var isFiltered: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsBinding.inflate(inflater, container, false)
        words = mutableListOf()
        allWords = mutableListOf()
        adapter = WordsAdapter(requireContext(), words, { word, view ->
            toggleFavorite(word)
        }, { word, view ->
            showContextMenu(word, view)
        })

        binding.list.adapter = adapter

        if (savedInstanceState == null) {
            loadWords()
        }

        setHasOptionsMenu(true)
        binding.fabClearFilter.setOnClickListener {
            showAllWords()
            binding.fabClearFilter.visibility = View.GONE
            isFiltered = false
        }
        return binding.root
    }
    private fun loadWords() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val loadedWords = withContext(Dispatchers.IO) {
                val statDao = database.statDao()
                database.wordDAO().getAll().onEach {
                    it.hits = statDao.countTotalByWord(it.uid!!, true)
                    it.misses = statDao.countTotalByWord(it.uid, false)
                }
            }
            words.clear()
            words.addAll(loadedWords)
            allWords.clear()
            allWords.addAll(loadedWords)
            adapter.notifyDataSetChanged()
            updateEmptyViewVisibility()
        }
    }

    fun showAllWords() {
        words.clear()
        words.addAll(allWords)
        adapter.notifyDataSetChanged()
        updateEmptyViewVisibility()
    }

    fun filterWordsByCategory(category: String) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val loadedWords = withContext(Dispatchers.IO) {
                database.wordDAO().getWordsByCategory(category)
            }
            if (loadedWords.isEmpty()) {
                showNoCategoryDialog(category)
            } else {
                words.clear()
                words.addAll(loadedWords)
                adapter.notifyDataSetChanged()
                binding.fabClearFilter.visibility = View.VISIBLE
                isFiltered = true
            }
        }
    }

    private fun showNoCategoryDialog(category: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("No Category Found")
            .setMessage("Sorry, there's no category of $category.")
            .setPositiveButton("OK", null)
            .show()
    }

    fun filterWordsByFavoriteStatus(isFavorite: Boolean) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val loadedWords = withContext(Dispatchers.IO) {
                database.wordDAO().getWordsByFavoriteStatus(isFavorite)
            }
            words.clear()
            words.addAll(loadedWords)
            adapter.notifyDataSetChanged()
            binding.fabClearFilter.visibility = View.VISIBLE
            isFiltered = true
        }
    }

    private fun toggleFavorite(word: Word) {
        word.isFavorite = !word.isFavorite
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).wordDAO().updateFavoriteStatus(word)
            withContext(Dispatchers.Main) {
                loadWords()
            }
        }
    }

    fun addWordToList(wordText: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).wordDAO().findByWord(wordText)
            withContext(Dispatchers.Main) {
                loadWords()
            }
        }
    }

    private fun showContextMenu(word: Word, view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.menu.add(0, 0, 0, "Search Synonyms")
        menu.menu.add(0, 1, 1, "Search Definitions")
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

    private fun updateEmptyViewVisibility() {
        binding.textViewListEmpty.visibility = if (words.isEmpty()) View.VISIBLE else View.GONE
    }
}
