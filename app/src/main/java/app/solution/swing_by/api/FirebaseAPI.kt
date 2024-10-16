package app.solution.swing_by.api

import android.util.Log
import app.solution.swing_by.MemoListAdapter
import app.solution.swing_by.MyApplication
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.item.MemoItem
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database


class FirebaseAPI {
    interface FirebaseCallback {
        fun successCallback()
        fun failureCallback()
    }

    companion object {
        private const val TAG = "SOL_LOG"
        private val FirebaseDatabase = Firebase.database.reference

        private lateinit var myAdapter: MemoListAdapter


        // 메모 리스트 갱신
        fun refreshMemoList(adapter: MemoListAdapter? = null, callback: FirebaseCallback? = null) {
            if (adapter != null) {
                myAdapter = adapter
            }

            FirebaseDatabase.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list = snapshot.children.map {
                            it.getValue(MemoItem::class.java)
                        }
                        myAdapter.submitList(list.toMutableList())

                        callback?.successCallback()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback?.failureCallback()
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
                        refreshMemoList()
                        callback?.successCallback()
                    } else {
                        Log.e(TAG, "registerMemo: ${it.exception?.stackTrace}")
                        callback?.failureCallback()
                    }
                }
        }
    }
}

