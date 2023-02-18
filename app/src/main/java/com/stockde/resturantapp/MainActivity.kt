package com.stockde.resturantapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.stockde.resturantapp.sharedpref.SharedData
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity() , Runnable{

    internal lateinit var mScan: Button
    internal lateinit var mPrint: Button
    internal lateinit var mDisc: Button
    internal lateinit var loadWebView: Button
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    internal lateinit var mBluetoothDevice: BluetoothDevice


    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            mBluetoothConnectProgressDialog!!.dismiss()
            Toast.makeText(this@MainActivity, "DeviceConnected", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    fun checkAndConnect(){
        var device = SharedData(this).getDevice()

        if(device != null && device.length > 0) {
            mBluetoothDevice = mBluetoothAdapter!!
                .getRemoteDevice(device)
            mBluetoothConnectProgressDialog = ProgressDialog.show(
                this,
                "Connecting...", mBluetoothDevice.name + " : "
                        + mBluetoothDevice.address, true, false
            )
            val mBlutoothConnectThread = Thread(this)
            mBlutoothConnectThread.start()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        checkAndConnect()


        mScan = findViewById(R.id.Scan) as Button
        loadWebView = findViewById<Button> (R.id.loadWebView)

        loadWebView.setOnClickListener {
            val intent =  Intent(this,com.stockde.resturantapp.webview.MainActivity::class.java)
            startActivity(intent)
        }
        mScan.setOnClickListener {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (mBluetoothAdapter == null) {
                Toast.makeText(this@MainActivity, "Message1", Toast.LENGTH_SHORT).show()
            } else {
                if (!mBluetoothAdapter!!.isEnabled) {
                    val enableBtIntent = Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(enableBtIntent,
                        REQUEST_ENABLE_BT)
                } else {
                    ListPairedDevices()
                    val connectIntent = Intent(this@MainActivity,
                        DeviceListActivity::class.java)
                    startActivityForResult(connectIntent,
                        REQUEST_CONNECT_DEVICE)
                }
            }
        }

        mPrint = findViewById(R.id.mPrint) as Button
        mPrint.setOnClickListener {
            val t = object : Thread() {
                override fun run() {
                    try {
                        val os = mBluetoothSocket!!.outputStream
                        var BILL = ""

                        BILL = ("                   XXXX MART    \n"
                                + "                   XX.AA.BB.CC.     \n " +
                                "                 NO 25 ABC ABCDE    \n" +
                                "                  XXXXX YYYYYY      \n" +
                                "                   MMM 590019091      \n")
                        BILL = BILL + "-----------------------------------------------\n"


                        BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel")
                        BILL = BILL + "\n"
                        BILL = BILL + "-----------------------------------------------"
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00")

                        BILL = BILL + "\n-----------------------------------------------"
                        BILL = BILL + "\n\n "

                        BILL = BILL + "                   Total Qty:" + "      " + "85" + "\n"
                        BILL = BILL + "                   Total Value:" + "     " + "700.00" + "\n"

                        BILL = BILL + "-----------------------------------------------\n"
                        BILL = BILL + "\n\n "
                        os.write(BILL.toByteArray())
                        //This is printer specific code you can comment ==== > Start

                        // Setting height
                        val gs = 29
                        os.write(intToByteArray(gs).toInt())
                        val h = 104
                        os.write(intToByteArray(h).toInt())
                        val n = 162
                        os.write(intToByteArray(n).toInt())

                        // Setting Width
                        val gs_width = 29
                        os.write(intToByteArray(gs_width).toInt())
                        val w = 119
                        os.write(intToByteArray(w).toInt())
                        val n_width = 2
                        os.write(intToByteArray(n_width).toInt())


                    } catch (e: Exception) {
                        Log.e("MainActivity", "Exe ", e)
                    }

                }
            }
            t.start()
        }

        mDisc = findViewById(R.id.dis) as Button
        mDisc.setOnClickListener {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter!!.disable()
        }

    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }

    }

    override fun onBackPressed() {
        try {
            if (mBluetoothSocket != null)
                mBluetoothSocket!!.close()
        } catch (e: Exception) {
            Log.e("Tag", "Exe ", e)
        }

        setResult(Activity.RESULT_CANCELED)
        finish()
    }


    @SuppressLint("MissingPermission")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CONNECT_DEVICE -> if (resultCode == Activity.RESULT_OK) {
                val mExtra = data?.extras
                val mDeviceAddress = mExtra!!.getString("DeviceAddress")
                Log.v(TAG, "Coming incoming address " + mDeviceAddress!!)
                mBluetoothDevice = mBluetoothAdapter!!
                    .getRemoteDevice(mDeviceAddress)
                SharedData(this).saveDevice(mDeviceAddress)
                mBluetoothConnectProgressDialog = ProgressDialog.show(this,
                    "Connecting...", mBluetoothDevice.name + " : "
                            + mBluetoothDevice.address, true, false)
                val mBlutoothConnectThread = Thread(this)
                mBlutoothConnectThread.start()
                // pairToDevice(mBluetoothDevice); This method is replaced by
                // progress dialog with thread
            }

            REQUEST_ENABLE_BT -> if (resultCode == Activity.RESULT_OK) {
                ListPairedDevices()
                val connectIntent = Intent(this@MainActivity,
                    DeviceListActivity::class.java)
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
            } else {
                Toast.makeText(this@MainActivity, "Message", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun ListPairedDevices() {
        try {
            val mPairedDevices = mBluetoothAdapter!!.bondedDevices
            if (mPairedDevices.size > 0) {
                for (mDevice in mPairedDevices) {
                    Log.v(TAG, "PairedDevices: " + mDevice.name + "  "
                            + mDevice.address)
                }
            }
        }catch (e: Exception){
        }

    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
            mHandler.sendEmptyMessage(0)
            if (!intent.getBooleanExtra("isFromChangePrinter",false)){
                val intent =  Intent(this,com.stockde.resturantapp.webview.MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        } catch (eConnectException: IOException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException)
            closeSocket(this!!.mBluetoothSocket!!)
            return
        }

    }

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            Log.d(TAG, "SocketClosed")
        } catch (ex: IOException) {
            Log.d(TAG, "CouldNotCloseSocket")
        }

    }

    fun sel(`val`: Int): ByteArray {
        val buffer = ByteBuffer.allocate(2)
        buffer.putInt(`val`)
        buffer.flip()
        return buffer.array()
    }

    companion object {
        protected val TAG = "TAG"
        private val REQUEST_CONNECT_DEVICE = 1
        private val REQUEST_ENABLE_BT = 2

        fun intToByteArray(value: Int): Byte {
            val b = ByteBuffer.allocate(4).putInt(value).array()

            for (k in b.indices) {
                println("Selva  [" + k + "] = " + "0x"
                        + UnicodeFormatter.byteToHex(b[k]))
            }

            return b[3]
        }
    }

}