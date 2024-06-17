package com.example.capstone_project.main

interface BottomNavigationViewController {
    fun hide(onCompleted: () -> Unit)
    fun show(onCompleted: () -> Unit)
}