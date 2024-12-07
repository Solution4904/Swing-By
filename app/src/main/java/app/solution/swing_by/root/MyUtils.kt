package app.solution.swing_by.root

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MyUtils {
    companion object {
        fun log(logType: LogType = LogType.DEBUG, detail: String) {
            val tag = "SOL_LOG"

            when (logType) {
                LogType.DEBUG -> Log.d(tag, detail)
                LogType.WARNING -> Log.w(tag, detail)
                LogType.ERROR -> Log.e(tag, detail)
            }
        }

        fun toast(context: Context, detail: String) {
            Toast.makeText(context, detail, Toast.LENGTH_SHORT).show()
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getDateTime(): String {
            return LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy년 MM월 dd일-HH시 mm분")
            )
        }

        const val SECRET_KEY = "ABCDEFGH12345678"
        val SECRET_IV = byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        @OptIn(ExperimentalEncodingApi::class)
        fun String.encryptCBC(): String {
            val iv = IvParameterSpec(SECRET_IV)
            val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key, iv)

            val crypted = cipher.doFinal(this.toByteArray())
            val encodedByte = Base64.encode(crypted, 0)

            return encodedByte.toString()
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun String.decryptCBC(): String {
            val iv = IvParameterSpec(SECRET_IV)
            val key = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key, iv)

            val decodedByte = Base64.decode(this, 0)
            val byteResult = cipher.doFinal(decodedByte)

            return String(byteResult)
        }
    }
}

enum class LogType {
    DEBUG,
    WARNING,
    ERROR,
}