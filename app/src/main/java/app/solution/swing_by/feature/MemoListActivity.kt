package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.swing_by.MemoListAdapter
import app.solution.swing_by.MyApplication
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ActivityMemoListBinding
import app.solution.swing_by.item.MemoItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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
        memoListAdapter = MemoListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memoListAdapter
        }

        FirebaseAPI.refreshMemoList(memoListAdapter)
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