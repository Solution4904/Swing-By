package app.solution.swing_by.api

import app.solution.swing_by.LogType
import app.solution.swing_by.MyApplication
import app.solution.swing_by.MyUtils
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.constant.LocalDataConstant
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FirebaseAPI {
    interface FirebaseCallback {
        fun successCallback(results: DataSnapshot? = null)
        fun successCallback(results: Task<AuthResult>)
        fun failureCallback()
    }

    companion object {
        private val FirebaseDatabase = Firebase.database.reference
        private val FirebaseAuth = Firebase.auth


        // 로그인 중인 계정의 UID 불러오기
        private fun validation(success: (String) -> Unit) {
            var uid = ""

            CoroutineScope(Dispatchers.Main).launch {
                uid = MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.UID)
            }.invokeOnCompletion {
                success(uid)
            }
        }

        // 메모 리스트 불러오기
        fun getMemoList(callback: FirebaseCallback? = null) {
            validation { uid ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(uid)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            callback?.successCallback(snapshot)
                            MyUtils.log(detail = "onDataChange: $snapshot")
                        }

                        override fun onCancelled(error: DatabaseError) {
                            callback?.failureCallback()
                            MyUtils.log(logType = LogType.ERROR, detail = "onCancelled: ${error.message}")
                        }
                    })
            }
        }

        // 메모 등록 & 수정
        fun registerMemo(memoUUID: String, memoModel: MutableMap<String, Any>, callback: FirebaseCallback? = null) {
            validation { uid ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(uid).child(memoUUID).setValue(memoModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback?.successCallback()
                        } else {
                            callback?.failureCallback()
                            MyUtils.log(logType = LogType.ERROR, detail = "registerMemo: ${it.exception?.stackTrace}")
                        }
                    }
            }
        }

        // 메모 삭제
        fun deleteMemo(memoUUID: String, callback: FirebaseCallback? = null) {
            validation { uid ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(uid).child(memoUUID).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback?.successCallback()
                        } else {
                            callback?.failureCallback()
                            MyUtils.log(logType = LogType.ERROR, detail = "deleteMemo: ${it.exception?.stackTrace}")
                        }
                    }
            }
        }

        // 이메일 로그인
        fun signIn(email: String, password: String, callback: FirebaseCallback? = null) {
            FirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback(it)
                    } else {
                        callback?.failureCallback()
                        MyUtils.log(detail = "signIn: ${it.exception?.stackTrace}")
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
                        callback?.failureCallback()
                        MyUtils.log(detail = "signUp: ${it.exception?.stackTrace}")
                    }
                }
        }
    }
}

