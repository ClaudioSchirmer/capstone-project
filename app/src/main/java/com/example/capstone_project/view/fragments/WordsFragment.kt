package com.example.capstone_project.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_hits -> {
                showHitsOnly()
                return true
            }
            R.id.sort_misses -> {
                showMissesOnly()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            adapter.updateWords(loadedWords)
            updateEmptyViewVisibility()
        }
    }

    private fun showAllWords() {
        words.clear()
        words.addAll(allWords)
        adapter.updateWords(allWords)
        adapter.showAll()
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
                adapter.updateWords(loadedWords)
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
            adapter.updateWords(loadedWords)
            binding.fabClearFilter.visibility = View.VISIBLE
            isFiltered = true
        }
    }

    private fun showHitsOnly() {
        val hitsOnly = allWords.filter { it.hits > 0 }
        words.clear()
        words.addAll(hitsOnly.sortedByDescending { it.hits })
        adapter.updateWords(words)
        adapter.showHitsOnly()  // Ensure adapter shows only hits
        binding.fabClearFilter.visibility = View.VISIBLE
        isFiltered = true
        updateEmptyViewVisibility()
    }

    private fun showMissesOnly() {
        val missesOnly = allWords.filter { it.misses > 0 }
        words.clear()
        words.addAll(missesOnly.sortedByDescending { it.misses })
        adapter.updateWords(words)
        adapter.showMissesOnly()  // Ensure adapter shows only misses
        binding.fabClearFilter.visibility = View.VISIBLE
        isFiltered = true
        updateEmptyViewVisibility()
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

    fun addWordToList(wordText: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(requireContext())
            val word = database.wordDAO().findByWord(wordText)
            withContext(Dispatchers.Main) {
                if (word != null) {
                    allWords.add(word)
                    words.clear()
                    words.addAll(allWords)
                    adapter.updateWords(words)
                    updateEmptyViewVisibility()
                }
            }
        }
    }

    fun searchWords(query: String) {
        if (!::allWords.isInitialized) {
            return
        }
        val filteredWords = allWords.filter { it.word.contains(query, ignoreCase = true) }
        words.clear()
        words.addAll(filteredWords)
        adapter.notifyDataSetChanged()
        updateEmptyViewVisibility()
    }
}

