package com.stockde.resturantapp

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.stockde.resturantapp.R

class DeviceListActivity : AppCompatActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    @SuppressLint("MissingPermission")
    private val mDeviceClickListener =
        AdapterView.OnItemClickListener { mAdapterView, mView, mPosition, mLong ->
            try {

                mBluetoothAdapter!!.cancelDiscovery()
                val mDeviceInfo = (mView as TextView).text.toString()
                val mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17)
                Log.v(TAG, "Device_Address " + mDeviceAddress)

                val mBundle = Bundle()
                mBundle.putString("DeviceAddress", mDeviceAddress)
                val mBackIntent = Intent()
                mBackIntent.putExtras(mBundle)
                setResult(Activity.RESULT_OK, mBackIntent)
                finish()
            } catch (ex: Exception) {

            }
        }

    @SuppressLint("MissingPermission")
    override fun onCreate(mSavedInstanceState: Bundle?) {
        super.onCreate(mSavedInstanceState)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setContentView(R.layout.device_list)

        try {

            setResult(Activity.RESULT_CANCELED)
            mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

            val mPairedListView = findViewById(R.id.paired_devices) as ListView
            mPairedListView.adapter = mPairedDevicesArrayAdapter
            mPairedListView.onItemClickListener = mDeviceClickListener

            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val mPairedDevices = mBluetoothAdapter!!.bondedDevices

            if (mPairedDevices.size > 0) {
                findViewById<TextView>(R.id.title_paired_devices).visibility = View.VISIBLE
                for (mDevice in mPairedDevices) {
                    mPairedDevicesArrayAdapter!!.add(mDevice.name + "\n" + mDevice.address)
                }
            } else {
                val mNoDevices = "None Paired"//getResources().getText(R.string.none_paired).toString();
                mPairedDevicesArrayAdapter!!.add(mNoDevices)
            }
        }catch (e: Exception){
            openAppNotificationSettings()
            Toast.makeText(this, "Please check your permissions. Please allow bluetooth permission from settings", Toast.LENGTH_LONG).show()
        }


    }

    fun openAppNotificationSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", getPackageName(), null)
        intent.data = uri
        startActivity(intent)
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter!!.cancelDiscovery()
            }
        }catch (e:Exception){

        }

    }

    companion object {
        protected val TAG = "TAG"
    }

}