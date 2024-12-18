package com.example.encryptiondemo

data class EncryptedPayload(
    val encryptedAesKey: String,
    val encryptedData: String,
    val iv: String
)
