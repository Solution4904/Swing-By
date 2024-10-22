package app.solution.swing_by.feature

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.swing_by.MemoListAdapter
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.databinding.ActivityMemoListBinding
import app.solution.swing_by.item.MemoItem
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

    private fun setMemoList() {
        memoListAdapter = MemoListAdapter(onClick = { item ->
            AlertDialog.Builder(this).apply {
                setTitle("메모를 삭제하시겠습니까?")
                setMessage("[${item.location}] ${item.title} \n${item.description}")
                setPositiveButton("네") { _, _ ->
                    FirebaseAPI.deleteMemo(item.uuid.toString(), object : FirebaseAPI.FirebaseCallback {
                        override fun successCallback(result: DataSnapshot) {}

                        override fun successCallback() {
                            Toast.makeText(context, "제거 완료", Toast.LENGTH_SHORT).show()

                            FirebaseAPI.refreshMemoList(object : FirebaseAPI.FirebaseCallback {
                                override fun successCallback(result: DataSnapshot) {
                                    val list = result.children.map {
                                        it.getValue(MemoItem::class.java)
                                    }
                                    memoListAdapter.submitList(list.toMutableList())
                                }

                                override fun successCallback() {}
                                override fun failureCallback() {}
                            })
                        }

                        override fun failureCallback() {
                            Toast.makeText(context, "제거 실패", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                setNegativeButton("아니오") { _, _ ->
//                            Toast.makeText(it.context, "Negative", Toast.LENGTH_SHORT).show()
                }
                create()
                show()
            }
        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memoListAdapter
        }

        FirebaseAPI.refreshMemoList(object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(result: DataSnapshot) {
                val list = result.children.map {
                    it.getValue(MemoItem::class.java)
                }
                memoListAdapter.submitList(list.toMutableList())
            }

            override fun successCallback() {}
            override fun failureCallback() {}
        })
    }

    private fun setButtons() {
        with(binding) {
            fbtnAdd.setOnClickListener {
                val intent = Intent(this@MemoListActivity, WriteMemoActivity::class.java)
                startActivity(intent)
            }
        }
    }
}