import com.example.encryptiondemo.DebugLog
import com.example.encryptiondemo.EncryptedPayload
import com.example.encryptiondemo.decodeBase64
import com.example.encryptiondemo.encodeBase64
import java.security.PublicKey

data class EncryptedResponse(
    val encryptedData: String,
    val iv: String
)


class Server {
    private val keyPair = EncryptionUtils.generateRSAKeyPair()
    val publicKey: PublicKey = keyPair.public
    private val privateKey = keyPair.private

    fun getPublicKeyString(): String {
        return EncryptionUtils.publicKeyToString(publicKey)
    }

    fun processClientRequest(jsonString: String): String {
        DebugLog.logi("     Server: Giải mã JSON payload")
        val payload: EncryptedPayload = EncryptionUtils.fromJson(jsonString)

        DebugLog.logi("     Server: Giải mã khóa AES bằng RSA private key")
        val aesKey = EncryptionUtils.decryptAESKeyWithRSA(
            payload.encryptedAesKey.decodeBase64(),
            privateKey
        )

        DebugLog.logi("     Server: Giải mã dữ liệu từ AES đã được giải mã bằng RSA private key")
        val decryptedData = EncryptionUtils.decryptDataWithAES(
            payload.encryptedData.decodeBase64(),
            aesKey,
            payload.iv.decodeBase64()
        )

        DebugLog.logi("Server received: $decryptedData")
        DebugLog.logi("4. Tạo phản hồi và mã hóa bằng AES")
        val response = "Hello, i'm Server. Have a nice day! Message from client: $decryptedData"
        val (encryptedResponse, responseIv) = EncryptionUtils.encryptDataWithAES(response, aesKey)

        DebugLog.logi("5. Trả về JSON string chứa phản hồi")
        val encryptedResponsePayload = EncryptedResponse(
            encryptedData = encryptedResponse.encodeBase64(),
            iv = responseIv.encodeBase64()
        )
        return EncryptionUtils.toJson(encryptedResponsePayload)
    }
}
