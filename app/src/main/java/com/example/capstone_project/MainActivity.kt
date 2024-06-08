package com.example.capstone_project

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstone_project.databinding.ActivityMainBinding
import com.example.capstone_project.view.fragments.AddWordFragment
import com.example.capstone_project.view.fragments.WordsFragment
import com.example.capstone_project.view.fragments.SettingsFragment


class MainActivity : AppCompatActivity(), AddWordFragment.onAddWord {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menu: Menu
    private var lastFragmentName: String? = "WordsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tabNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_list -> {
                    menu.clear()
                    menuInflater.inflate(R.menu.words, menu)
                    WordsFragment().show()
                }

                R.id.bottom_menu_settings -> {
                    menu.clear()
                    SettingsFragment().show()
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu!!
        val inflater = menuInflater
        inflater.inflate(R.menu.words, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_addItem -> {
                val addWordFragment = AddWordFragment()
                addWordFragment.show(supportFragmentManager, "addWordFragment")
            }
        }
        return true
    }

    override fun onAddWord(word: String) {
        val wordsFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
        wordsFragment?.addWordToList(word)
        val addWordFragment =
            supportFragmentManager.findFragmentByTag("addWordFragment") as? AddWordFragment
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
}