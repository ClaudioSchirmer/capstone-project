package com.example.capstone_project.main

//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import androidx.fragment.app.Fragment
//import com.example.capstone_project.PreferenceUtil
//import com.example.capstone_project.R
//import com.example.capstone_project.databinding.ActivityMainBinding
//import com.example.capstone_project.view.fragments.AddWordFragment
//import com.example.capstone_project.view.fragments.PlayFragment
//import com.example.capstone_project.view.fragments.SettingsFragment
//import com.example.capstone_project.view.fragments.WordsFragment
//
//
//class MainActivity : AppCompatActivity(), AddWordFragment.OnAddWord,
//    BottomNavigationViewController {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var menu: Menu
//    private var lastFragmentName: String? = "WordsFragment"
//
//    companion object {
//        lateinit var preferences: PreferenceUtil
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        preferences = PreferenceUtil(binding.root.context)
//        setContentView(binding.root)
//
//        binding.tabNavigation.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.bottom_menu_list -> {
//                    menu.clear()
//                    menuInflater.inflate(R.menu.words, menu)
//                    WordsFragment().show()
//                }
//
//                R.id.bottom_menu_play -> {
//                    menu.clear()
//                    PlayFragment().show()
//                }
//
//                R.id.bottom_menu_settings -> {
//                    menu.clear()
//                    SettingsFragment().show()
//                }
//            }
//            true
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        this.menu = menu!!
//        val inflater = menuInflater
//        inflater.inflate(R.menu.words, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.menu_addItem -> {
//                val addWordFragment = AddWordFragment()
//                addWordFragment.show(supportFragmentManager, "addWordFragment")
//            }
//        }
//        return true
//    }
//
//    override fun onAddWord(word: String) {
//        val wordsFragment =
//            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? WordsFragment
//        wordsFragment?.addWordToList(word)
//        val addWordFragment =
//            supportFragmentManager.findFragmentByTag("addWordFragment") as? AddWordFragment
//        addWordFragment?.dismiss()
//    }
//
//    private fun Fragment.show() {
//        with(supportFragmentManager.beginTransaction()) {
//            val fragmentToUpdate = this@show::class.simpleName
//            if (fragmentToUpdate != lastFragmentName) {
//                replace(R.id.fragmentContainerView, this@show)
//                lastFragmentName = fragmentToUpdate
//                commit()
//            }
//        }
//    }
//
//    override fun hide(onCompleted: () -> Unit) {
//        with(binding.tabNavigation) {
//            animate().translationY((binding.tabNavigation.height).toFloat())
//                .setDuration(500)
//                .withEndAction {
//                    visibility = View.GONE
//                    onCompleted()
//                }
//                .start();
//        }
//    }
//
//    override fun show(onCompleted: () -> Unit) {
//        with(binding.tabNavigation) {
//            visibility = View.VISIBLE
//            animate().translationY(0F)
//                .setDuration(500)
//                .withEndAction {
//                    onCompleted()
//                }
//                .start();
//        }
//    }
//
//}

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstone_project.PreferenceUtil
import com.example.capstone_project.R
import com.example.capstone_project.databinding.ActivityMainBinding
import com.example.capstone_project.view.fragments.AddWordFragment
import com.example.capstone_project.view.fragments.PlayFragment
import com.example.capstone_project.view.fragments.SettingsFragment
import com.example.capstone_project.view.fragments.WordsFragment

class MainActivity : AppCompatActivity(), AddWordFragment.OnAddWord,
    BottomNavigationViewController {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menu: Menu
    private var lastFragmentName: String? = "WordsFragment"

    companion object {
        lateinit var preferences: PreferenceUtil
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        preferences = PreferenceUtil(binding.root.context)
        setContentView(binding.root)

        binding.tabNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_menu_list -> {
                    menu.clear()
                    menuInflater.inflate(R.menu.words, menu)
                    WordsFragment().show()
                }

                R.id.bottom_menu_play -> {
                    menu.clear()
                    PlayFragment().show()
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
