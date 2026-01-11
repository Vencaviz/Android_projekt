package com.projekt.xvizvary

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartBudgetApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

