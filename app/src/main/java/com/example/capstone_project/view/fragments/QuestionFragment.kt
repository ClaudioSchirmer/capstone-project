package com.example.capstone_project.view.fragments

import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
import com.example.capstone_project.application.word.AddStatCommand
import com.example.capstone_project.application.word.AddStatCommandHandler
import com.example.capstone_project.databinding.FragmentQuestionBinding
import com.example.capstone_project.helper.ConstraintsHelper
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Stat
import com.example.capstone_project.infrastructure.data.entities.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import kotlin.math.sqrt
import kotlin.random.Random
import com.example.capstone_project.infrastructure.data.dao.Word as WordDao
import com.example.capstone_project.infrastructure.data.dao.Stat as StatDao

class QuestionFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentQuestionBinding
    private lateinit var words: List<Word>
    private var param1: String? = null
    private lateinit var currentWord: Word
    private lateinit var wordDao: WordDao
    private lateinit var statDao: StatDao
    private var pass: Int = 0
    private var fail: Int = 0
    private var total: Int = 0
    private lateinit var sensorManager: SensorManager
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f
    private var lastShakeTime: Long = 0
    private var lastQuestionResult: Boolean? = null

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
            wordDao = AppDatabase(requireContext()).wordDAO()
            words = wordDao.getAll()
            launch(Dispatchers.Main) {
                nextQuestion()
            }
        }
        binding.radioGroupOptions.setOnCheckedChangeListener(::onCheckedChange)

        // Initialize shake detection
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        acceleration = SensorManager.GRAVITY_EARTH
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister shake detection
        sensorManager.unregisterListener(this)
    }

    private fun onCheckedChange(group: ViewGroup, checkedId: Int) {
        val itemClicked = binding.root.findViewById<RadioButton>(checkedId)
        val wordToUpdate = currentWord.copy()

        if (itemClicked.text == wordToUpdate.word) {
            wordToUpdate.pass++
            pass++
            lastQuestionResult = true
        } else {
            wordToUpdate.fail++
            fail++
            lastQuestionResult = false
        }

        lifecycleScope.launch(Dispatchers.IO) {
            wordDao.updatePass(wordToUpdate)
        }

        lifecycleScope.launch(Dispatchers.IO) {

            val cal = Calendar.getInstance()
            cal.time = Date()

            AddStatCommandHandler(requireContext()).insertOrUpdate(
                AddStatCommand(
                    dateInfo = ConstraintsHelper.dfDateInfo.format(cal.time),
                    wordUid = wordToUpdate.uid,
                    isRemember = lastQuestionResult
                )
            )
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

                showFeedback(lastQuestionResult)
                delay(1000)
                hideFeedback()

                binding.textViewDefinition.visibility = View.VISIBLE
                binding.radioGroupOptions.visibility = View.VISIBLE
            }
        }
    }

    private fun showFeedback(result: Boolean?) {
        if (result != null) {
            if (result == true) {
                binding.textViewResult.text = getString(R.string.question_result_pass, pass)
                binding.imageViewResultPass.visibility = View.VISIBLE
            } else {
                binding.textViewResult.text = getString(R.string.question_result_fail, fail)
                binding.imageViewResultFail.visibility = View.VISIBLE
            }
            binding.textViewResult.visibility = View.VISIBLE
            binding.layoutResult.visibility = View.VISIBLE
        }
    }

    private fun hideFeedback() {
        binding.imageViewResultPass.visibility = View.GONE
        binding.imageViewResultFail.visibility = View.GONE
        binding.textViewResult.visibility = View.GONE
        binding.layoutResult.visibility = View.GONE
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER && ::currentWord.isInitialized && event.values[0].toDouble() != 0.0 && !currentWord.tip.isNullOrBlank()) {
            Log.d("Claudio", event.values[0].toString())
            Log.d("Claudio", event.values[1].toString())
            Log.d("Claudio", event.values[2].toString())

            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            lastAcceleration = currentAcceleration
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            val currentTime = System.currentTimeMillis()
            if (acceleration > 5 && currentTime - lastShakeTime > 500) { // Reduced threshold and debounce time
                lastShakeTime = currentTime
                // Show the tip in a toast
                Toast.makeText(context, currentWord.tip, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Do nothing
    }
}
