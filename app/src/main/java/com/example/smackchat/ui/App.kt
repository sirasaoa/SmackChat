package com.example.smackchat.ui

import android.app.Application
import android.content.SharedPreferences
import com.example.smackchat.utilities.SharedPrefs

class App: Application(){
    companion object{
        lateinit var prefs: SharedPrefs
    }
    override fun onCreate() {
        prefs = SharedPrefs(applicationContext)
        super.onCreate()

    }
}