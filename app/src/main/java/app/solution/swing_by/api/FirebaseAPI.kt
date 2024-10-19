package app.solution.swing_by.api

import android.util.Log
import app.solution.swing_by.MyApplication
import app.solution.swing_by.constant.FirebaseConstant
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class FirebaseAPI {
    interface FirebaseCallback {
        fun successCallback(result: DataSnapshot? = null)
        fun failureCallback()
    }

    companion object {
        private const val TAG = "SOL_LOG"
        private val FirebaseDatabase = Firebase.database.reference
        private val FirebaseAuth = Firebase.auth


        // 메모 리스트 갱신
        fun refreshMemoList(callback: FirebaseCallback) {
            FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        callback.successCallback(snapshot)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback.failureCallback()
                    }
                })
        }

        // 메모 등록 & 수정
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

        // 메모 삭제
        fun deleteMemo(memoUUID: String, callback: FirebaseCallback? = null) {
            FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid).child(memoUUID).removeValue()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback()
                    } else {
                        Log.e(TAG, "registerMemo: ${it.exception?.stackTrace}")
                        callback?.failureCallback()
                    }
                }
        }

        // 이메일 로그인
        fun signIn(email: String, password: String, callback: FirebaseCallback? = null) {
            FirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        MyApplication.userUid = it.result.user?.uid.toString()

                        callback?.successCallback()
                    } else {
                        Log.d(TAG, "signIn: ${it.exception?.stackTrace}")
                        callback?.failureCallback()
                    }
                }
        }

        // 이메일 가입
        fun signUp(email: String, password: String, callback: FirebaseCallback? = null) {
            FirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback()
                    } else {
                        Log.d(TAG, "signUp: ${it.exception?.stackTrace}")
                        callback?.failureCallback()
                    }
                }
        }
    }
}

