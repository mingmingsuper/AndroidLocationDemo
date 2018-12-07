package com.lmm.locationdemo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log

open class LocationUtils private constructor() {

    companion object {

        private var context:Context? = null
        private var mListener:((Location?)->Unit)? = null

        private fun checkPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }

            return true
        }

        private fun getProvider(context: Context): String? {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.isSpeedRequired = false
            criteria.isAltitudeRequired = false
            criteria.isBearingRequired = false
            criteria.isCostAllowed = false
            criteria.powerRequirement = Criteria.POWER_LOW
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.getBestProvider(criteria, true)
        }

        open fun isGpsEnabled(context: Context) : Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return gps || network
        }

        private val netListener: LocationListener = object : LocationListener {
            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onLocationChanged(location: Location) {
                mListener?.invoke(location)
                removeListener()
            }
        }

        private val gpsListener: LocationListener = object : LocationListener {
            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onLocationChanged(location: Location) {
                mListener?.invoke(location)
                removeListener()
            }
        }

        private fun removeListener() {
            val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            locationManager?.removeUpdates(netListener)
            locationManager?.removeUpdates(gpsListener)
        }

        open fun getLastLocation(context: Context,callback:(Location?)->Unit) {
            mListener = callback
            this.context = context
            if (!checkPermission(context)) {
                return
            }
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                mListener?.invoke(location)
               return
            }
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                mListener?.invoke(location)
                return
            }

            location = locationManager.getLastKnownLocation(getProvider(context))
            if (location != null) {
                mListener?.invoke(location)
                return
            }

            if (location == null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,gpsListener)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0f,netListener)
            }



            Log.e("获取定位失败", "xxxxxxxxxxxxxxx")
        }
    }
}