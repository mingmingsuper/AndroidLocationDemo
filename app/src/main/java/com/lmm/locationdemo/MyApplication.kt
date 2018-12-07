package com.lmm.locationdemo

import android.app.Application
import com.baidu.mapapi.SDKInitializer

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        SDKInitializer.initialize(applicationContext)
    }
}