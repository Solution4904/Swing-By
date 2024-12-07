package app.solution.swing_by.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ItemMemoBinding
import app.solution.swing_by.feature.WriteMemoActivity
import app.solution.swing_by.item.MemoItem


class MemoListAdapter(val onClick: ((MemoItem) -> Unit)) : ListAdapter<MemoItem, MemoListAdapter.ViewHolder>(differ) {
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
                tvLocation.text = item.location
                tvDescription.text = item.description
                tvTime.text = item.currentTime!!.replace("-", "\n")


                root.setOnClickListener {
                    val intent = Intent(it.context, WriteMemoActivity::class.java).apply {
                        putExtra(FirebaseConstant.DB_MEMO_UUID, item.uuid)
                        putExtra(FirebaseConstant.DB_MEMO_DESCRIPTION, item.description.toString())
                        putExtra(FirebaseConstant.DB_MEMO_LOCATION, item.location.toString())
                        putExtra(FirebaseConstant.DB_MEMO_CATEGORY, item.category)
                    }
                    it.context.startActivity(intent)
                }

                root.setOnLongClickListener {
                    onClick.invoke(item)

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