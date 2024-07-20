package com.example.capstone_project.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.capstone_project.R
import com.example.capstone_project.application.ApplicationNotification
import com.example.capstone_project.application.word.AddWordCommand
import com.example.capstone_project.application.word.AddWordCommandHandler
import com.example.capstone_project.databinding.FragmentAddWordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddWordFragment : DialogFragment() {

    private lateinit var binding: FragmentAddWordBinding
    private lateinit var onAddWordHandler: OnAddWord

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAddWordHandler = context as? OnAddWord ?: OnAddWord {
            Log.d(this::class.simpleName, "onAddWordHandler has not been defined!")
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddWordBinding.inflate(inflater, container, false)

        setupCategorySpinner()

        binding.textViewTitle.text = getString(R.string.add_new_word)
        binding.textViewErrorDefinition.visibility = View.INVISIBLE
        binding.textViewErrorWord.visibility = View.INVISIBLE

        binding.buttonSave.setOnClickListener(::onSaveTapped)
        binding.imageButtonClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupCategorySpinner() {
        val categories = listOf("Select Category") + resources.getStringArray(R.array.category_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun onSaveTapped(view: View) {
        val category = binding.spinnerCategory.selectedItem.toString()
        if (category == "Select Category") {
            showToast("Please select a valid category")
            return
        }

        binding.textViewErrorDefinition.visibility = View.INVISIBLE
        binding.textViewErrorWord.visibility = View.INVISIBLE
        binding.buttonSave.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val word = binding.editTextWord.text.toString()
                AddWordCommandHandler(requireContext()).invoke(
                    AddWordCommand(
                        word = word,
                        tip = binding.editTextTip.text.toString(),
                        definition = binding.editTextDefinition.text.toString(),
                        category = category
                    )
                )
                launch(Dispatchers.Main) {
                    onAddWordHandler.onAddWord(word)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    handleErrors(it)
                    binding.buttonSave.isEnabled = true
                }
            }
        }
    }

    private fun handleErrors(throwable: Throwable) {
        if (throwable is ApplicationNotification) {
            throwable.Notifications.forEach {
                when (it.fieldName) {
                    "word" -> showError(binding.textViewErrorWord, it.message)
                    "definition" -> showError(binding.textViewErrorDefinition, it.message)
                }
            }
        }
    }

    private fun showError(textView: TextView, message: String) {
        textView.text = message
        textView.visibility = View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    internal fun interface OnAddWord {
        fun onAddWord(word: String)
    }
}
