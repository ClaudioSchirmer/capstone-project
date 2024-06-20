package com.example.capstone_project

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String?):String?{
        return preferences.getString(key,defValue)
    }

    fun setString(key: String, defValue: String?){
        preferences.edit().putString(key, defValue).apply()
    }
}