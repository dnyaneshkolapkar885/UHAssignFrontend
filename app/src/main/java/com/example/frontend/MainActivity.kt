package com.example.frontend

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_mode).setOnClickListener { sendPostRequest("mode", "Eco") }
        findViewById<Button>(R.id.button_clean).setOnClickListener { sendPostRequest("clean", null) }
        findViewById<Button>(R.id.button_maintenance).setOnClickListener { sendGetRequest("maintenance") }
        findViewById<Button>(R.id.button_support).setOnClickListener { sendGetRequest("support") }
        findViewById<Switch>(R.id.switch_power).setOnCheckedChangeListener { _, isChecked ->
            sendPostRequest("power", if (isChecked) "on" else "off")
        }
    }

    private fun sendPostRequest(endpoint: String, param: String?) {
        val json = param?.let { JSONObject().put(endpoint, it).toString() } ?: "{}"
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://192.168.0.105:5000/$endpoint")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.body?.string())
            }
        })
    }

    private fun sendGetRequest(endpoint: String) {
        val request = Request.Builder()
            .url("http://192.168.0.105:5000/$endpoint")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.body?.string())
            }
        })
    }
}
