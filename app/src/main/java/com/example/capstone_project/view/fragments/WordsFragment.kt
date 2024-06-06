package com.example.capstone_project.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
import com.example.capstone_project.databinding.FragmentWordsBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordsFragment : ListFragment() {

    private lateinit var adapter: ArrayAdapter<*>
    private lateinit var words: MutableList<String>
    private lateinit var binding: FragmentWordsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordsBinding.inflate(layoutInflater)
        lifecycleScope.launch(Dispatchers.IO) {
            words = AppDatabase(requireContext()).WordDAO().getAll().map { it.word }.toMutableList()
            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                words
            )
            launch(Dispatchers.Main) {
                listAdapter = adapter
                if (words.isNotEmpty()) {
                    binding.textViewListEmpty.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    fun addWordToList(word: String) {
        words.add(word)
        adapter.notifyDataSetChanged()
        binding.textViewListEmpty.visibility = View.GONE
    }
}