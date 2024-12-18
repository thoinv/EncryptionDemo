import com.example.encryptiondemo.DebugLog
import com.example.encryptiondemo.EncryptedPayload
import com.example.encryptiondemo.decodeBase64
import com.example.encryptiondemo.encodeBase64
import java.security.PublicKey
import javax.crypto.SecretKey


class Client(private val serverPublicKey: PublicKey) {
    private lateinit var aesKey: SecretKey // Giữ lại khóa AES cho phản hồi, có thể kết hợp sử dung cơ chế gen bằng keystore với Android version >= 23

    fun sendData(clientMessage: String): String {
        DebugLog.logi("     Client: Tạo khóa AES")
        aesKey = EncryptionUtils.generateAESKey()

        DebugLog.logi("     Client: Mã hóa khóa AES bằng RSA public key của server")
        val encryptedAesKey = EncryptionUtils.encryptAESKeyWithRSA(aesKey, serverPublicKey)

        DebugLog.logi("     Client: Mã hóa dữ liệu bằng AES")
        val (encryptedData, iv) = EncryptionUtils.encryptDataWithAES(clientMessage, aesKey)

        DebugLog.logi("     Client: Tạo payload dạng JSON")
        val payload = EncryptedPayload(
            encryptedAesKey = encryptedAesKey.encodeBase64(),
            encryptedData = encryptedData.encodeBase64(),
            iv = iv.encodeBase64()
        )
        return EncryptionUtils.toJson(payload)
    }

    fun processServerResponse(jsonResponse: String) {
        DebugLog.logi("     Giải mã JSON payload nhận được từ Server")
        val responsePayload: EncryptedResponse = EncryptionUtils.fromJson(jsonResponse)

        DebugLog.logi("     Giải mã dữ liệu từ AES")
        val decryptedResponse = EncryptionUtils.decryptDataWithAES(
            responsePayload.encryptedData.decodeBase64(),
            aesKey,
            responsePayload.iv.decodeBase64()
        )

        DebugLog.logi("     Hiển thị phản hồi từ Server")
        DebugLog.logd("Client received: $decryptedResponse")
    }
}
