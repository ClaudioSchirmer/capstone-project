package com.example.capstone_project.view.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
import com.example.capstone_project.databinding.FragmentPlayBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.main.BottomNavigationViewController
import com.example.capstone_project.view.ChooseCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayFragment : Fragment(), ChooseCategory.onClick {

    private lateinit var binding: FragmentPlayBinding
    private var bottomNavigationViewController: BottomNavigationViewController? = null
    private var numberOfWords: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bottomNavigationViewController = context as? BottomNavigationViewController
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayBinding.inflate(layoutInflater)
        binding.buttonPlay.setOnClickListener(::onPlay)
        binding.buttonPlayCategory.setOnClickListener(::onPlayByCategory)
        binding.buttonStop.setOnClickListener(::onStopPlaying)
        binding.buttonStop.visibility = View.INVISIBLE
        binding.textViewPlayWith.visibility = View.INVISIBLE
        binding.textViewPlayWithNumber.visibility = View.INVISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            numberOfWords = AppDatabase(requireContext()).wordDAO().count()
            launch(Dispatchers.Main) {
                binding.textViewPlayWithNumber.text = getString(R.string.words_available, numberOfWords.toString())
                binding.textViewPlayWith.visibility = View.VISIBLE
                binding.textViewPlayWithNumber.visibility = View.VISIBLE
            }
        }
        return binding.root
    }

    private fun onPlayByCategory(view: View) {
        ChooseCategory(this).show(parentFragmentManager, "ChooseCategory")
    }

    override fun onCategorySelected(category: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val numberOfWordsByCategory = AppDatabase(requireContext()).wordDAO().countByCategory(category)
            lifecycleScope.launch(Dispatchers.Main) {
                if (numberOfWordsByCategory < 4) {
                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Missing Words")
                        setMessage("You should have at least 4 words registered in the category $category before starting to play")
                        setPositiveButton("Ok") { dialog, _ ->
                            dialog.dismiss()
                        }
                    }.create().show()
                } else {
                    onPlay(null, category)
                }
            }

        }
    }

    private fun onPlay(view: View?, category: String? = null) {
        if (numberOfWords < 4) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Missing Words")
                setMessage("You should have at least 4 words registered before starting to play")
                setPositiveButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }
            }.create().show()
        } else {
            binding.buttonPlay.visibility = View.INVISIBLE
            binding.buttonPlayCategory.visibility = View.INVISIBLE
            binding.buttonPlay.alpha = 0f
            binding.buttonPlayCategory.alpha = 0f
            binding.textViewPlayWith.visibility = View.INVISIBLE
            binding.textViewPlayWith.alpha = 0f
            binding.textViewPlayWithNumber.visibility = View.INVISIBLE
            binding.textViewPlayWithNumber.alpha = 0f
            bottomNavigationViewController?.hide {
                with(childFragmentManager.beginTransaction()) {
                    replace(R.id.fragmentContainerPlay, QuestionFragment(category), "QuestionFragment")
                    commit()
                }
                binding.buttonStop.visibility = View.VISIBLE
                binding.buttonStop.animate().alpha(1f).setDuration(1000).start()
            }
        }
    }

    private fun onStopPlaying(view: View) {
        binding.buttonStop.visibility = View.INVISIBLE
        binding.buttonStop.alpha = 0f
        childFragmentManager.findFragmentByTag("QuestionFragment")?.let { fragment: Fragment ->
            with(childFragmentManager.beginTransaction()) {
                remove(fragment)
                commit()
            }
        }
        bottomNavigationViewController?.show {
            binding.buttonPlay.visibility = View.VISIBLE
            binding.buttonPlay.animate().alpha(1f).setDuration(1000).start()
            binding.buttonPlayCategory.visibility = View.VISIBLE
            binding.buttonPlayCategory.animate().alpha(1f).setDuration(1000).start()
            binding.textViewPlayWith.visibility = View.VISIBLE
            binding.textViewPlayWith.animate().alpha(1f).setDuration(1000).start()
            binding.textViewPlayWithNumber.visibility = View.VISIBLE
            binding.textViewPlayWithNumber.animate().alpha(1f).setDuration(1000).start()
        }
    }
}