package com.example.encryptiondemo

import Client
import Server
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.KeyboardUtils

class MainActivity : AppCompatActivity() {
    private lateinit var server: Server

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
        val edMessage = findViewById<EditText>(R.id.ed_message)

        DebugLog.logi("Server generate RSA keypair")
        server = Server()

        DebugLog.logi("1. Client request đến server để lấy public key dạng string")
        val rsaPublicKeyStringFromServer = server.getPublicKeyString()
        DebugLog.logd("Client Nhận được public key từ server: \n$rsaPublicKeyStringFromServer\nClient lưu trữ vào bộ nhớ để tái sử dung cho các lần request sau")

        findViewById<View>(R.id.bt_send).setOnClickListener {
            tvMessage.text = ""
            KeyboardUtils.hideSoftInput(this)
            demo(edMessage.text.toString(), rsaPublicKeyStringFromServer)
        }
    }

    private fun demo(clientMessage: String, rsaPublicKeyStringFromServer: String) {

        DebugLog.logi("2. Convert public key string sang PublicKey để sử dụng cho Android")
        val rsaServerPublicKey = EncryptionUtils.stringToPublicKey(rsaPublicKeyStringFromServer)

        DebugLog.logi("3. Client gửi request đến server với data json")
        val client = Client(rsaServerPublicKey)
        val clientRequest = client.sendData(clientMessage)

        DebugLog.logd("     Client sent JSON: $clientRequest")

        DebugLog.logi("4. Server nhận dữ liệu từ client, xử lý và gửi phản hồi")
        val serverResponse = server.processClientRequest(clientRequest)

        DebugLog.logd("     Server sent JSON: $serverResponse")

        DebugLog.logi("5. Client nhận phản hồi và giải mã")
        client.processServerResponse(serverResponse)

    }
}