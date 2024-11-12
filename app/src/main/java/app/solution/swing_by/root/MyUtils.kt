package app.solution.swing_by.root

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    }
}

enum class LogType {
    DEBUG,
    WARNING,
    ERROR,
}