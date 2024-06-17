package com.example.capstone_project.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.databinding.FragmentQuestionBinding
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.capstone_project.infrastructure.data.dao.Word as WordDao

class QuestionFragment : Fragment() {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var words: List<Word>
    private var param1: String? = null
    private lateinit var currentWord: Word
    private lateinit var wordDao: WordDao
    private var pass: Int = 0
    private var fail: Int = 0
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("a")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuestionBinding.inflate(layoutInflater)
        binding.textViewFail.visibility = View.INVISIBLE
        binding.textViewFailNumber.visibility = View.INVISIBLE
        binding.textViewPass.visibility = View.INVISIBLE
        binding.textViewPassNumber.visibility = View.INVISIBLE
        binding.textViewPlayedWith.visibility = View.INVISIBLE
        binding.textViewPlayedWithNumber.visibility = View.INVISIBLE
        hide()
        lifecycleScope.launch(Dispatchers.IO) {
            wordDao = AppDatabase(requireContext()).WordDAO()
            words = wordDao.getAll()
            launch(Dispatchers.Main) {
                nextQuestion()
            }
        }
        binding.radioGroupOptions.setOnCheckedChangeListener(::onCheckedChange)
        return binding.root
    }

    private fun onCheckedChange(group: ViewGroup, checkedId: Int) {
        val itemClicked = binding.root.findViewById<RadioButton>(checkedId)
        val wordToUpdate = currentWord.copy()
        if (itemClicked.text == wordToUpdate.word) {
            wordToUpdate.pass++
            pass++
        } else {
            wordToUpdate.fail++
            fail++
        }
        lifecycleScope.launch(Dispatchers.IO) {
            wordDao.updatePass(wordToUpdate)
        }
        binding.radioButtonOption1.isChecked = false
        binding.radioButtonOption2.isChecked = false
        binding.radioButtonOption3.isChecked = false
        binding.radioButtonOption4.isChecked = false
        nextQuestion()
    }

    private fun nextQuestion() {
        hide()
        val question = getNewQuestion()
        question?.let {
            total++
            binding.textViewDefinition.text = question.definition
            binding.radioButtonOption1.text = question.options[0]
            binding.radioButtonOption2.text = question.options[1]
            binding.radioButtonOption3.text = question.options[2]
            binding.radioButtonOption4.text = question.options[3]
            show()
        } ?: run {
            binding.textViewPassNumber.text = pass.toString()
            binding.textViewFailNumber.text = fail.toString()
            binding.textViewPlayedWithNumber.text = total.toString()
            binding.progressBar.visibility = View.INVISIBLE
            if (fail > 0) {
                binding.textViewFail.visibility = View.VISIBLE
                binding.textViewFailNumber.visibility = View.VISIBLE
            }
            if (pass > 0) {
                binding.textViewPass.visibility = View.VISIBLE
                binding.textViewPassNumber.visibility = View.VISIBLE
            }
            binding.textViewPlayedWith.visibility = View.VISIBLE
            binding.textViewPlayedWithNumber.visibility = View.VISIBLE
        }
    }

    private fun getNewQuestion(): Question? {
        val optionsRemaining = words.count { !it.used }
        if (optionsRemaining > 0) {
            val questionNumber =
                getRamdon(0, words.count(), words.indices.filter { words[it].used })
            val word = words[questionNumber]
            if (!word.used) {
                word.used = true
                currentWord = word
                val avoidAnswerIndex = mutableListOf<Int>()
                val avoidAnswerWordIndex = mutableListOf<Int>()
                val options = Array(4) { "None" }
                var option = getRamdon(0, 4, avoidAnswerIndex)
                currentWord.word.let {
                    options[option] = it
                }
                avoidAnswerIndex.add(option)
                avoidAnswerWordIndex.add(questionNumber)
                do {
                    option = getRamdon(0, 4, avoidAnswerIndex)
                    val newAnswerIndex = getRamdon(0, words.count(), avoidAnswerWordIndex)
                    words[newAnswerIndex].word.let {
                        options[option] = it
                    }
                    avoidAnswerIndex.add(option)
                    avoidAnswerWordIndex.add(newAnswerIndex)
                } while (avoidAnswerIndex.count() <= 3)
                return Question(word.definition, options.toList())
            }
        }
        return null
    }

    private fun getRamdon(from: Int, until: Int, ignore: List<Int>): Int {
        var random = 0
        do {
            random = Random.nextInt(from, until)
        } while (ignore.contains(random))
        return random
    }

    private inner class Question(
        val definition: String,
        val options: List<String>
    )

    private fun hide() {
        binding.progressBar.visibility = View.VISIBLE
        binding.textViewDefinition.visibility = View.INVISIBLE
        binding.radioGroupOptions.visibility = View.INVISIBLE
    }

    private fun show() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            lifecycleScope.launch(Dispatchers.Main) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.textViewDefinition.visibility = View.VISIBLE
                binding.radioGroupOptions.visibility = View.VISIBLE
            }
        }
    }
}