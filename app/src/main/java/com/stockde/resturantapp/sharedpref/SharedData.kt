package com.stockde.resturantapp.sharedpref

import android.content.Context
import android.content.SharedPreferences

class SharedData {
    private var sharedPreference: SharedPreferences? = null
    companion object{
        var sharedName = "DeviceData"
        var deviceKey = "DeviceAddress"
        var urlKey = "Url"
    }
    constructor(context: Context){
        sharedPreference = context.getSharedPreferences(sharedName,Context.MODE_PRIVATE)
    }

    fun saveDevice(mDeviceAddress: String){
        var editor = sharedPreference?.edit()
        editor?.putString(deviceKey,mDeviceAddress)
        editor?.commit()
    }

    fun getDevice(): String {
        return sharedPreference?.getString(deviceKey,"").toString()
    }

    fun saveUrl(url: String){
        var editor = sharedPreference?.edit()
        editor?.putString(urlKey,url)
        editor?.commit()
    }

    fun getUrl(): String {
        return sharedPreference?.getString(urlKey,"http://ecom.stockde.com/").toString()
    }
}