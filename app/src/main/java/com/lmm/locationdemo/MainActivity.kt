package com.lmm.locationdemo

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.baidu.mapapi.SDKInitializer
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.utils.CoordinateConverter

class MainActivity : AppCompatActivity() {

    private var mLocationManager: LocationManager? = null
    //    private var mProvider: String? = null
    private val _requestPermissionCode = 1000
    private val _settingGPSCode = 1001
    private var mGPSText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        location()
    }

    private fun initView() {
        mGPSText = findViewById(R.id.gps)
    }

//    private fun getLocationManager() {
//        if (mLocationManager != null) {
//            return
//        }
//        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//    }


//    private fun getProvider():String? {
//        val criteria = Criteria()
//        criteria.accuracy = Criteria.ACCURACY_FINE
//        criteria.isSpeedRequired = false
//        criteria.isAltitudeRequired = false
//        criteria.isBearingRequired = false
//        criteria.isCostAllowed = false
//        criteria.powerRequirement = Criteria.POWER_LOW
//        getLocationManager()
//        return mLocationManager?.getBestProvider(criteria, true)
//    }

    private fun tipSettingGPS() {
        AlertDialog.Builder(this).setMessage("请打开GPS").setPositiveButton("确定") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, _settingGPSCode) // 设置完成后返回到原来的界面
        }.setNegativeButton("取消") { dialog:DialogInterface, _:Int ->
            dialog.dismiss()
            finish()
        }.setCancelable(false).show()

    }

    private fun location() {
        if (!LocationUtils.isGpsEnabled(this)) {
            // 转到手机设置界面，用户设置GPS
            tipSettingGPS()
            return
        }
        if (!checkPermission()) {
            return
        }
        LocationUtils.getLastLocation(this) {
            it?.let {
                val converter = CoordinateConverter()
                converter.from(CoordinateConverter.CoordType.GPS)
                converter.coord(LatLng(it.latitude, it.longitude))
                val desLatLng = converter.convert()
                desLatLng?.let {
                    val gpsStr = "纬度为：" + desLatLng!!.latitude + ",经度为：" + desLatLng!!.longitude
                    mGPSText?.text = gpsStr
                }
            }
        }


//        getLocationManager()
//        var location = mLocationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//        if(location != null) {
//            Log.e("获取到了定位", "NETWORK_PROVIDER")
//            return
//        }
//        location = mLocationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        if(location != null) {
//            Log.e("获取到了定位", "GPS_PROVIDER")
//            return
//        }


//        mProvider = getProvider()
//
//        if (mProvider != null) {
//            val location = mLocationManager?.getLastKnownLocation(mProvider!!)
//            if (location != null) {
//                val converter = CoordinateConverter()
//                converter.from(CoordinateConverter.CoordType.GPS)
//                converter.coord(LatLng(location.latitude, location.longitude))
//                val desLatLng = converter.convert()
//                val gpsStr = "纬度为：" + desLatLng!!.latitude + ",经度为：" + desLatLng!!.longitude
//                mGPSText?.text = gpsStr
//            }
//            mLocationManager?.requestLocationUpdates(mProvider!!, 2000, 2f, locationListener)
//        }
    }

//    private val locationListener: LocationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            val converter = CoordinateConverter()
//            converter.from(CoordinateConverter.CoordType.GPS)
//            converter.coord(LatLng(location.latitude, location.longitude))
//            val desLatLng = converter.convert()
//            val gpsStr = "纬度为：" + desLatLng!!.latitude + ",经度为：" + desLatLng!!.longitude
//            mGPSText?.text = gpsStr
//        }
//
//        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//        override fun onProviderEnabled(provider: String) {}
//        override fun onProviderDisabled(provider: String) {}
//    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermission()
                return false
            }
        }

        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            _requestPermissionCode
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == _requestPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                location()
            } else {
                requestPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("onActivityResult ======", "$requestCode ===== $resultCode")
        if (requestCode == _settingGPSCode) {
            location()
        }
    }
}
