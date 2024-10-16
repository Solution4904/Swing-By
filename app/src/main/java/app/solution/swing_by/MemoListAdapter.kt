package app.solution.swing_by

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ItemMemoBinding
import app.solution.swing_by.feature.WriteMemoActivity
import app.solution.swing_by.item.MemoItem

class MemoListAdapter : ListAdapter<MemoItem, MemoListAdapter.ViewHolder>(differ) {
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

    inner class ViewHolder(private val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MemoItem) {
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
                        setTitle("메모를 삭제하시겠습니까?")
                        setMessage("[${item.location}] ${item.title} \n${item.description}")
                        setPositiveButton("네") { _, _ ->
                            FirebaseAPI.deleteMemo(item.uuid.toString(), object : FirebaseAPI.FirebaseCallback {
                                override fun successCallback() {
                                    Toast.makeText(it.context, "제거 완료", Toast.LENGTH_SHORT).show()
                                }

                                override fun failureCallback() {
                                    Toast.makeText(it.context, "제거 실패", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                        setNegativeButton("아니오") { _, _ ->
//                            Toast.makeText(it.context, "Negative", Toast.LENGTH_SHORT).show()
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
}