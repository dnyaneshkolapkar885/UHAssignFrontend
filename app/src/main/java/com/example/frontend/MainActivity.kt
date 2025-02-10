package com.example.frontend

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.frontend.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val client = OkHttpClient()
    private val BASE_URL = "http://192.168.50.129:5000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupModeRadioButtons()
        setupCleanButton()
        setupMaintenanceButton()
        setupSupportButton()
        setupPowerButton()
        setupPresetRadioButtons()
    }

    private fun setupPresetRadioButtons() {
        binding.radioGroupPreset.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.presetClean -> {
                    handlePreset("Clean")
                }
                R.id.presetDeepClean -> {
                    handlePreset("Deep clean")
                }
            }
        }
    }

    private fun handlePreset(preset: String) {
        Toast.makeText(this, "$preset preset Selected", Toast.LENGTH_SHORT).show()
        val request = createPostRequest("preset", preset)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.optString("status", "error")
                    val presetResponse = jsonResponse.optString("preset", "unknown")
                    runOnUiThread {
                        if (status == "success") {
                            Toast.makeText(this@MainActivity, "Preset changed to: $presetResponse", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error: ${jsonResponse.optString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun setupPowerButton() {
        binding.switchPower.setOnCheckedChangeListener { _, isChecked ->
            val state = if(isChecked) "on" else "off"
            Toast.makeText(this, "Turning $state device", Toast.LENGTH_SHORT).show()
            val request = createPostRequest("power", if (isChecked) "on" else "off")
            client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.optString("status", "error")
                    val power = jsonResponse.optString("power", "unknown")
                    runOnUiThread {
                        if (status == "success") {
                            Toast.makeText(this@MainActivity, "Device turned : $power", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error: ${jsonResponse.optString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
        }
    }

    private fun setupSupportButton() {
        binding.buttonSupport.setOnClickListener {
            val request = createGetRequest("support")
            client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val contact = jsonResponse.optString("contact", "")
                    val phone = jsonResponse.optString("phone", "")
                    runOnUiThread {
                        // create dialog
                        AlertDialog.Builder(this@MainActivity)
                            .setMessage("Please contact on \n" +
                                    "Email : $contact \n" +
                                    "Phone : $phone")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss() // Close the dialog when OK is clicked
                            }
                            .show()
                    }
                }
            }
        })
        }

    }


    private fun setupMaintenanceButton() {
        binding.buttonMaintenance.setOnClickListener {
            handleCommonButton("maintenance")
        }
    }

    private fun setupCleanButton() {
        binding.buttonClean.setOnClickListener {
            handleCommonButton("clean")
        }
    }

    private fun handleCommonButton(buttonName: String) {
        val request = createGetRequest(buttonName)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.optString("status", "error")
                    runOnUiThread {
                        if (status.isNotBlank()) {
                            Toast.makeText(this@MainActivity, status, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error: ${jsonResponse.optString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun setupModeRadioButtons() {
        binding.radioGroupMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioEco -> {
                    handleMode("Eco")
                }
                R.id.radioPower -> {
                    handleMode("Power")
                }
                R.id.radioManual -> {
                    handleMode("Manual")
                }
            }
        }
    }

    private fun handleMode(mode: String) {
        Toast.makeText(this, "$mode Mode Selected", Toast.LENGTH_SHORT).show()
        val request = createPostRequest("mode", mode)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    val status = jsonResponse.optString("status", "error")
                    val modeResponse = jsonResponse.optString("mode", "unknown")
                    runOnUiThread {
                        if (status == "success") {
                            Toast.makeText(this@MainActivity, "Mode changed to: $modeResponse", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@MainActivity, "Error: ${jsonResponse.optString("error")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun createPostRequest(endpoint: String, param: String?): Request {
        val json = param?.let { JSONObject().put(endpoint, it).toString() } ?: "{}"
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        return Request.Builder()
            .url("$BASE_URL/$endpoint")
            .post(body)
            .build()
    }

    private fun createGetRequest(endpoint: String): Request {
        return Request.Builder()
            .url("$BASE_URL/$endpoint")
            .get()
            .build()
    }
}
