package com.example.capstone_project.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.PreferenceUtil
import com.example.capstone_project.R
import com.example.capstone_project.databinding.ActivityMainBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.view.fragments.AddWordFragment
import com.example.capstone_project.view.fragments.PlayFragment
import com.example.capstone_project.view.fragments.SettingsFragment
import com.example.capstone_project.view.fragments.StatsFragment
import com.example.capstone_project.view.fragments.WordsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), AddWordFragment.OnAddWord,
    BottomNavigationViewController {

    private lateinit var binding: ActivityMainBinding
    private var menu: Menu? = null
    private var lastFragmentName: String? = "WordsFragment"

    companion object {
        lateinit var preferences: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        preferences = PreferenceUtil(binding.root.context)
        setContentView(binding.root)
        WordsFragment().show()

        binding.tabNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_list -> {
                    menu?.clear()
                    menuInflater.inflate(R.menu.words, menu)
                    WordsFragment().show()
                }
                R.id.bottom_menu_play -> {
                    menu?.clear()
                    PlayFragment().show()
                }
                R.id.bottom_menu_stats -> {
                    menu?.clear()
                    StatsFragment().show()
                }
                R.id.bottom_menu_settings -> {
                    menu?.clear()
                    SettingsFragment().show()
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        val inflater = menuInflater
        if (lastFragmentName == "WordsFragment") {
            inflater.inflate(R.menu.words, menu)

            val searchItem = menu?.findItem(R.id.action_search)
            val searchView = searchItem?.actionView as? SearchView

            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        searchWords(it)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        searchWords(it)
                    }
                    return true
                }
            })
        }
        return true
    }

    private fun searchWords(query: String) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
        fragment?.searchWords(query)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_addItem -> {
                val addWordFragment = AddWordFragment()
                addWordFragment.show(supportFragmentManager, "addWordFragment")
                true
            }
            R.id.action_enter_category -> {
                showCategoryInputDialog()
                true
            }
            R.id.action_filter_favorites -> {
                filterWordsByFavoriteStatus(true)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCategoryInputDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_category_input, null)
        val input = dialogView.findViewById<EditText>(R.id.editTextCategory)
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val category = input.text.toString().trim()
                if (category.isNotBlank()) {
                    val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
                    if (fragment != null) {
                        lifecycleScope.launch {
                            val database = AppDatabase.getDatabase(this@MainActivity)
                            val loadedWords = withContext(Dispatchers.IO) {
                                database.wordDAO().getWordsByCategory(category)
                            }
                            if (loadedWords.isEmpty()) {
                                showNoCategoryFoundDialog(category)
                            } else {
                                fragment.filterWordsByCategory(category)
                            }
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun showNoCategoryFoundDialog(category: String) {
        AlertDialog.Builder(this)
            .setTitle("No Category Found")
            .setMessage("Sorry, there's no category of \"$category\".")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun filterWordsByFavoriteStatus(isFavorite: Boolean) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
        fragment?.filterWordsByFavoriteStatus(isFavorite)
    }

    override fun onAddWord(word: String) {
        val wordsFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
        wordsFragment?.addWordToList(word)
        val addWordFragment = supportFragmentManager.findFragmentByTag("addWordFragment") as? AddWordFragment
        addWordFragment?.dismiss()
    }

    private fun Fragment.show() {
        with(supportFragmentManager.beginTransaction()) {
            val fragmentToUpdate = this@show::class.simpleName
            if (fragmentToUpdate != lastFragmentName) {
                replace(R.id.fragmentContainerView, this@show)
                lastFragmentName = fragmentToUpdate
                commit()
            }
        }
    }
    override fun hide(onCompleted: () -> Unit) {
        with(binding.tabNavigation) {
            animate().translationY((binding.tabNavigation.height).toFloat())
                .setDuration(500)
                .withEndAction {
                    visibility = View.GONE
                    onCompleted()
                }
                .start()
        }
    }
    override fun show(onCompleted: () -> Unit) {
        with(binding.tabNavigation) {
            visibility = View.VISIBLE
            animate().translationY(0F)
                .setDuration(500)
                .withEndAction {
                    onCompleted()
                }
                .start()
        }
    }
}
