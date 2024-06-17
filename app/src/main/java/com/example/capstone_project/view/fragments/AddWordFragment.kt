package com.example.capstone_project.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddWordBinding.inflate(layoutInflater)

        binding.textViewTitle.text = getString(R.string.add_new_word)
        binding.textViewErrorDefinition.visibility = View.INVISIBLE
        binding.textViewErrorWord.visibility = View.INVISIBLE

        binding.buttonSave.setOnClickListener(::onSaveTapped)
        binding.imageButtonClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun onSaveTapped(view: View) {
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
                        definition = binding.editTextTextDefinition.text.toString()
                    )
                )
                launch(Dispatchers.Main) {
                    onAddWordHandler.onAddWord(word)
                }
            }.onFailure {
                launch(Dispatchers.Main) {
                    if (it is ApplicationNotification) {
                        it.Notifications.firstOrNull { it.fieldName == "word" }?.let {
                            binding.textViewErrorWord.text = it.message
                            binding.textViewErrorWord.visibility = View.VISIBLE
                        }
                        it.Notifications.firstOrNull { it.fieldName == "definition" }?.let {
                            binding.textViewErrorDefinition.text = it.message
                            binding.textViewErrorDefinition.visibility = View.VISIBLE
                        }
                    }
                    binding.buttonSave.isEnabled = true
                }
            }
        }
    }

    internal fun interface OnAddWord {
        fun onAddWord(word:String)
    }
}