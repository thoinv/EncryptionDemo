import android.util.Base64
import com.google.gson.Gson
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    val gson = Gson()

    // Tạo cặp khóa RSA
    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.genKeyPair()
    }

    // Tạo khóa AES
    fun generateAESKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    // Mã hóa khóa AES bằng RSA
    fun encryptAESKeyWithRSA(aesKey: SecretKey, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(aesKey.encoded)
    }

    // Giải mã khóa AES bằng RSA
    fun decryptAESKeyWithRSA(encryptedAesKey: ByteArray, privateKey: PrivateKey): SecretKey {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decodedKey = cipher.doFinal(encryptedAesKey)
        return SecretKeySpec(decodedKey, "AES")
    }

    // Mã hóa dữ liệu bằng AES
    fun encryptDataWithAES(data: String, aesKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = IvParameterSpec(ByteArray(16)) // Vector khởi tạo
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, iv)
        val encryptedData = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Pair(encryptedData, iv.iv)
    }

    // Convert PublicKey to String (Base64)
    fun publicKeyToString(publicKey: PublicKey): String {
        return Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
    }

    // Convert String to PublicKey
    fun stringToPublicKey(publicKeyString: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }

    // Giải mã dữ liệu bằng AES
    fun decryptDataWithAES(encryptedData: ByteArray, aesKey: SecretKey, iv: ByteArray): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val ivParameterSpec = IvParameterSpec(iv)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec)
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData, Charsets.UTF_8)
    }

    // Encode JSON
    fun toJson(data: Any): String = gson.toJson(data)

    // Decode JSON
    inline fun <reified T> fromJson(json: String): T = gson.fromJson(json, T::class.java)
}
