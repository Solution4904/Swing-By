package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.swing_by.MemoListAdapter
import app.solution.swing_by.databinding.ActivityMemoListBinding
import app.solution.swing_by.item.MemoItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MemoListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemoListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMemoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMemoList()
        setButtons()
    }

    private fun setMemoList() {
        val memoListAdapter = MemoListAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = memoListAdapter
        }

        val currentUser = Firebase.auth.currentUser
        val currentUid = currentUser?.uid.orEmpty()
        Firebase.database.reference.child(currentUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.map {
                        it.getValue(MemoItem::class.java)
                    }
                    memoListAdapter.submitList(list)
                }

                override fun onCancelled(error: DatabaseError) {}
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