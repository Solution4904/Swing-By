package app.solution.swing_by.api

import app.solution.swing_by.root.LogType
import app.solution.swing_by.root.MyApplication
import app.solution.swing_by.root.MyUtils
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
    interface CallbackByAuthResult {
        fun successCallback(results: Task<AuthResult>)
        fun failureCallback()
    }

    interface CallbackByDataSnapshot {
        fun successCallback(results: DataSnapshot? = null)
        fun failureCallback()
    }

    interface Callback {
        fun successCallback()
        fun failureCallback()
    }

    companion object {
        private val FirebaseAuth = Firebase.auth
        private val FirebaseDatabase = Firebase.database.reference


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
        fun getMemoList(callback: CallbackByDataSnapshot? = null) {
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
        fun registerMemo(memoUUID: String, memoModel: MutableMap<String, Any>, callback: Callback? = null) {
            validation { uid ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(uid).child(memoUUID).setValue(memoModel)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback?.successCallback()
                        } else {
                            callback?.failureCallback()
                            MyUtils.log(logType = LogType.ERROR, detail = "registerMemo: ${it.exception?.message}")
                        }
                    }
            }
        }

        // 메모 삭제
        fun deleteMemo(memoUUID: String, callback: CallbackByDataSnapshot? = null) {
            validation { uid ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(uid).child(memoUUID).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback?.successCallback()
                        } else {
                            MyUtils.log(logType = LogType.ERROR, detail = "deleteMemo: ${it.exception?.stackTrace}")

                            callback?.failureCallback()
                        }
                    }
            }
        }

        // # 로그인
        fun signIn(email: String, password: String, callback: CallbackByAuthResult? = null) {
            FirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback(it)
                    } else {
                        MyUtils.log(detail = "signIn: ${it.exception?.stackTrace}")

                        callback?.failureCallback()
                    }
                }
        }

        // # 가입
        fun signUp(email: String, password: String, callback: Callback? = null) {
            FirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback?.successCallback()
                    } else {
                        MyUtils.log(detail = "signUp: ${it.exception?.stackTrace}")

                        callback?.failureCallback()
                    }
                }
        }

        // 회원 탈퇴
        fun deleteAccount(callback: Callback) {
            FirebaseAuth.currentUser?.let { user ->
                FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(user.uid).removeValue()
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            user.delete().continueWith { task ->
                                if (task.isSuccessful) {
                                    MyUtils.log(detail = "success")

                                    callback.successCallback()
                                } else {
                                    MyUtils.log(detail = "canceled")

                                    callback.failureCallback()
                                }
                            }
                        } else {
                            MyUtils.log(detail = "failure")

                            callback.failureCallback()
                        }
                    }

            }
        }

        // 로그아웃
        fun logout() {
            FirebaseAuth.signOut()
        }
    }
}