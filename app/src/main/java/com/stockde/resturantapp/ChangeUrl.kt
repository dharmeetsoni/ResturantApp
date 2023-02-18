package com.stockde.resturantapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.webkit.URLUtil
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.stockde.resturantapp.sharedpref.SharedData


class ChangeUrl: AppCompatActivity() {

    lateinit var edtUrl: EditText
    lateinit var btnSave: Button

    override fun onBackPressed() {
        val intent = Intent(this@ChangeUrl,com.stockde.resturantapp.webview.MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_url)

        edtUrl = findViewById(R.id.edtUrl)
        btnSave = findViewById(R.id.btnSave)
        val shared = SharedData(this)

        edtUrl.setText(shared.getUrl())

        btnSave.setOnClickListener {
            val urlStr = edtUrl.text.toString()
            if (urlStr.length > 0){
                val isValid = URLUtil.isValidUrl(urlStr) && Patterns.WEB_URL.matcher(urlStr)
                    .matches()
                if (!isValid){
                    Toast.makeText(this,"Please enter valid URL",Toast.LENGTH_LONG).show()
                }else{
                    // save url
                    shared.saveUrl(urlStr)
                    val intent = Intent(this@ChangeUrl,com.stockde.resturantapp.webview.MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }else{
                Toast.makeText(this,"Please enter valid URL",Toast.LENGTH_LONG).show()
            }
        }

    }
}