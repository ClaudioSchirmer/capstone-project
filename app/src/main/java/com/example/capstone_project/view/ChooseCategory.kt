package com.example.capstone_project.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.capstone_project.R

class ChooseCategory(val onClickImpl: ChooseCategory.onClick) : DialogFragment() {

    private var checkedItem: Int = 0
    lateinit var categories: Array<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categories = resources.getStringArray(R.array.category_options)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity)
        .apply {
            setTitle("Categories")
            setSingleChoiceItems(categories.map { it }.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            setPositiveButton("Ok") { _, _ ->
                onClickImpl.onCategorySelected(categories[checkedItem])
            }
            setNegativeButton(R.string.cancel) { _, _ ->

            }
        }.create()

    fun interface onClick {
        fun onCategorySelected(category: String)
    }
}