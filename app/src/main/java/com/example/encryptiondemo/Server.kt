import android.util.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class Server {

    private val aesKey: SecretKey = generateAESKey()
    private val rsaKeyPair: KeyPair = generateRSAKeyPair()

    // 1. Tạo cặp khóa RSA
    private fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    // 2. Tạo AES Key
    private fun generateAESKey(): SecretKey {
        val keyBytes = "1234567890123456".toByteArray()
        return SecretKeySpec(keyBytes, "AES")
    }

    // 3. Mã hóa dữ liệu bằng AES Key
    private fun encryptData(data: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encryptedData = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    // 4. Mã hóa AES Key bằng RSA Private Key
    private fun encryptAESKeyWithRSA(): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, rsaKeyPair.private)
        val encryptedAESKey = cipher.doFinal(aesKey.encoded)
        return Base64.encodeToString(encryptedAESKey, Base64.DEFAULT)
    }

    // 5. Gửi Public Key cho Client và dữ liệu JSON
    fun handleRequest(): Map<String, String> {
        val encryptedData = encryptData("Dữ liệu phản hồi từ Server!")
        val encryptedAESKey = encryptAESKeyWithRSA()
        val serverPublicKey = Base64.encodeToString(rsaKeyPair.public.encoded, Base64.DEFAULT)

        return mapOf(
            "encryptedData" to encryptedData,
            "encryptedAESKey" to encryptedAESKey,
            "serverPublicKey" to serverPublicKey
        )
    }
}
