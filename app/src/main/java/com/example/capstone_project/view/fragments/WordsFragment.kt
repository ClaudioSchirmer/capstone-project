package com.example.capstone_project.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.databinding.FragmentWordsBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordsFragment : ListFragment() {

    private lateinit var adapter: ArrayAdapter<String>
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
                registerForContextMenu(listView)
            }
        }
        return binding.root
    }

    fun addWordToList(word: String) {
        words.add(word)
        adapter.notifyDataSetChanged()
        binding.textViewListEmpty.visibility = View.GONE
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        v.showContextMenu()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val info = menuInfo as AdapterView.AdapterContextMenuInfo
        menu.setHeaderTitle(words[info.position])
        menu.add(Menu.NONE, 0, 0, "Search Synonyms")
        menu.add(Menu.NONE, 1, 1, "Search Definitions")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val word = words[info.position]
        when (item.itemId) {
            0 -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=synonyms+of+$word"))
                startActivity(intent)
                return true
            }
            1 -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=definition+of+$word"))
                startActivity(intent)
                return true
            }
        }
        return super.onContextItemSelected(item)
    }
}
