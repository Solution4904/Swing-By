package app.solution.swing_by

import android.content.Context
import android.util.Log
import android.widget.Toast

class MyUtils {
    companion object {

        fun log(logType: LogType = LogType.DEBUG, detail: String) {
            val TAG = "SOL_LOG"

            when (logType) {
                LogType.DEBUG -> Log.d(TAG, detail)
                LogType.WARNING -> Log.w(TAG, detail)
                LogType.ERROR -> Log.e(TAG, detail)
            }
        }

        fun toast(context: Context, detail: String) {
            Toast.makeText(context, detail, Toast.LENGTH_SHORT).show()
        }
    }
}

enum class LogType {
    DEBUG,
    WARNING,
    ERROR,
}