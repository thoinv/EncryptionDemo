import com.google.gson.Gson
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import com.example.encryptiondemo.DebugLog

class Client {

    // 1. Giải mã AES Key bằng RSA Public Key
    fun decryptAESKey(encryptedAESKey: String, serverPublicKey: String): SecretKey {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKeySpec = X509EncodedKeySpec(Base64.decode(serverPublicKey, Base64.DEFAULT))
        val rsaPublicKey = keyFactory.generatePublic(publicKeySpec)

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey)
        val decodedAESKey = cipher.doFinal(Base64.decode(encryptedAESKey, Base64.DEFAULT))

        return SecretKeySpec(decodedAESKey, "AES")
    }

    // 2. Giải mã dữ liệu bằng AES Key
    fun decryptData(encryptedData: String, aesKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedData = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
        return String(decryptedData)
    }

    fun handleServerResponse(jsonResponse: String) {
        val responseMap = Gson().fromJson(jsonResponse, Map::class.java)

        val encryptedAESKey = responseMap["encryptedAESKey"] as String
        val encryptedData = responseMap["encryptedData"] as String
        val serverPublicKey = responseMap["serverPublicKey"] as String

        DebugLog.logi("Giải mã AES Key")
        val aesKey = decryptAESKey(encryptedAESKey, serverPublicKey)
        DebugLog.logd("AES Key đã giải mã: ${Base64.encodeToString(aesKey.encoded, Base64.DEFAULT)}")

        DebugLog.logi("Giải mã dữ liệu")
        val decryptedData = decryptData(encryptedData, aesKey)
        DebugLog.logd("Dữ liệu giải mã từ Server: $decryptedData")
    }
}
