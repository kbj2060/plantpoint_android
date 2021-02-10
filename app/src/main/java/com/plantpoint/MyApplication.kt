package com.plantpoint

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex




class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    fun context(): Context = applicationContext
}