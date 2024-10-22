package app.solution.swing_by.feature

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.swing_by.adapter.MemoListAdapter
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.databinding.ActivityMemoListBinding
import app.solution.swing_by.item.MemoItem
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot


class MemoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoListBinding
    private lateinit var memoListAdapter: MemoListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    override fun onStart() {
        super.onStart()

        setMemoList()
    }

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            fbtnAdd.setOnClickListener {
                Intent(this@MemoListActivity, WriteMemoActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    // # 메모 리스트 불러오기
    private fun setMemoList() {
        val callback = object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(result: DataSnapshot?) {
                result?.let {
                    val elements = it.children.map { snapshot ->
                        snapshot.getValue(MemoItem::class.java)
                    }
                    memoListAdapter.submitList(elements.toMutableList())
                }
            }

            override fun successCallback(result: Task<AuthResult>) {}
            override fun failureCallback() {}
        }
        FirebaseAPI.refreshMemoList(callback)

        memoListAdapter = MemoListAdapter {
            AlertDialog.Builder(this)
                .setTitle("메모를 삭제하시겠습니까?")
                .setMessage("[${it.location}] ${it.title} \n${it.description}")
                .setPositiveButton("네") { _, _ ->
                    FirebaseAPI.deleteMemo(it.uuid.toString(), callback)
                    Toast.makeText(this@MemoListActivity, "메모를 삭제했습니다.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("아니오") { _, _ ->
                    Toast.makeText(this@MemoListActivity, "작업을 취소했습니다.", Toast.LENGTH_SHORT).show()
                }
                .create()
                .show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memoListAdapter
        }
    }
}