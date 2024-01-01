package com.example.caremademo

import android.app.Application

val Profile: ProfilePreference by lazy {
    PhotoApplication.prefs!!
}
class PhotoApplication : Application(){
    companion object {
        var prefs: ProfilePreference? = null
    }
    override fun onCreate() {
        super.onCreate()
        prefs = ProfilePreference(this)
//        prefs!!.baseUrl="http://192.168.56.1//:80"
    }

}