package com.example.encryptiondemo

import Client
import Server
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvMessage = findViewById<TextView>(R.id.tv_message)
        DebugLog.callback = { message ->
            tvMessage.append(message + "\n")
        }
        demo()
    }

    private fun demo() {
        DebugLog.logi("Khởi tạo Server và xử lý yêu cầu")
        val server = Server()
        val jsonResponse = Gson().toJson(server.handleRequest())
        println("Server trả về JSON: $jsonResponse")

        DebugLog.logi("Khởi tạo Client và xử lý JSON phản hồi")
        val client = Client()
        client.handleServerResponse(jsonResponse)
    }
}