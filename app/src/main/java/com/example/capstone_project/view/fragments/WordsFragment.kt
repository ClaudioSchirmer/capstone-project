package com.example.capstone_project.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.adapters.WordAdapter
import com.example.capstone_project.databinding.FragmentWordsBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WordsFragment : ListFragment() {

    private lateinit var adapter: WordAdapter
    private lateinit var words: MutableList<String>
    private lateinit var binding: FragmentWordsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordsBinding.inflate(layoutInflater, container, false)
        words = mutableListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            val dbWords = AppDatabase(requireContext()).WordDAO().getAll().map { it.word }.toMutableList()
            withContext(Dispatchers.Main) {
                words.addAll(dbWords)
                adapter.notifyDataSetChanged()
                if (words.isNotEmpty()) {
                    binding.textViewListEmpty.visibility = View.GONE
                }
            }
        }
        adapter = WordAdapter(requireContext(), words)

        binding.list.adapter = adapter
        return binding.root
    }

    fun addWordToList(word: String) {
        words.add(word)
        adapter.notifyDataSetChanged()
        binding.textViewListEmpty.visibility = View.GONE
    }
}