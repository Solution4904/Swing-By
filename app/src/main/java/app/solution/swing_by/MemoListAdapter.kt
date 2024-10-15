package app.solution.swing_by

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ItemMemoBinding
import app.solution.swing_by.feature.WriteMemoActivity
import app.solution.swing_by.item.MemoItem
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database

class MemoListAdapter(private val valueEventListener: ValueEventListener) : ListAdapter<MemoItem, MemoListAdapter.ViewHolder>(differ) {
    private lateinit var uuid: String

    inner class ViewHolder(private val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MemoItem) {
            uuid = item.uuid.toString()
            with(binding) {
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvLocation.text = item.location

                root.setOnClickListener {
                    val intent = Intent(it.context, WriteMemoActivity::class.java).apply {
                        putExtra(FirebaseConstant.DB_MEMO_UUID, item.uuid)
                        putExtra(FirebaseConstant.DB_MEMO_TITLE, item.title.toString())
                        putExtra(FirebaseConstant.DB_MEMO_DESCRIPTION, item.description.toString())
                        putExtra(FirebaseConstant.DB_MEMO_LOCATION, item.location.toString())
                        putExtra(FirebaseConstant.DB_MEMO_CATEGORY, item.category)
                    }
                    it.context.startActivity(intent)
                }

                root.setOnLongClickListener {
                    AlertDialog.Builder(it.context).apply {
                        setTitle("Title")
                        setMessage("Message")
                        setPositiveButton("OK") { p0, p1 ->
//                            Toast.makeText(it.context, "Positive", Toast.LENGTH_SHORT).show()

                            Firebase.database.reference.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid).child(uuid).removeValue()
                                .addOnCompleteListener { result ->
                                    if (result.isSuccessful) {
                                        com.google.firebase.ktx.Firebase.database.reference.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid)
                                            .addListenerForSingleValueEvent(valueEventListener)
                                        Toast.makeText(it.context, "제거 완료", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(it.context, "제거 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        setNegativeButton("Cancel") { p0, p1 ->
                            Toast.makeText(it.context, "Negative", Toast.LENGTH_SHORT).show()
                        }
                        create()
                        show()
                    }

                    return@setOnLongClickListener true
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMemoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<MemoItem>() {
            override fun areItemsTheSame(oldItem: MemoItem, newItem: MemoItem): Boolean {
                return oldItem.uuid == newItem.uuid
            }

            override fun areContentsTheSame(oldItem: MemoItem, newItem: MemoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}