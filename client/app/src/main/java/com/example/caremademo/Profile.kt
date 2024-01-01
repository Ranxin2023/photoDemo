package com.example.caremademo

import android.content.Context
import android.content.SharedPreferences

class ProfilePreference (var context: Context? = null){
    private val prefsFilename = "com.example.socketdemo.prefs"
    private var prefs: SharedPreferences? = null
    val baseUrl="http://192.168.108.1:80"
    val socketBaseUrl="http://192.168.108.1:5000"
    init {
        this.prefs = context?.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    }
    var photoId: String
        set(value) {
            this.prefs?.edit()?.putString("sid", value)?.apply()
        }
        get() = prefs?.getString("sid", "") ?: ""

}