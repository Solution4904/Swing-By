package app.solution.swing_by.api

import android.util.Log
import app.solution.swing_by.MyApplication
import app.solution.swing_by.constant.FirebaseConstant
import com.google.firebase.Firebase
import com.google.firebase.database.database


class FirebaseAPI {
    interface FirebaseCallback {
        fun successCallback()
        fun failureCallback()
    }

    companion object {
        private const val TAG = "SOL_LOG"
        private val FirebaseDatabase = Firebase.database.reference

        // 메모 등록
        fun registerMemo(memoUUID: String, memoModel: MutableMap<String, Any>, callback: FirebaseCallback? = null) {
            FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid).child(memoUUID).setValue(memoModel)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback()
                    } else {
                        Log.e(TAG, "registerMemo: ${it.exception?.stackTrace}")
                        callback?.failureCallback()
                    }
                }
        }

        // 메모 수정
    }
}

