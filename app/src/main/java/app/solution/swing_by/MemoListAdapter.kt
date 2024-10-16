package app.solution.swing_by

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

class MemoListAdapter : ListAdapter<MemoItem, MemoListAdapter.ViewHolder>(differ) {
    private lateinit var uuid: String

    inner class ViewHolder(private val binding: ItemMemoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MemoItem) {
            uuid = item.uuid.toString()
            with(binding) {
                tvTitle.text = item.title
                tvDescription.text = item.description
                tvLocation.text = item.location
            }

            binding.root.setOnClickListener {
                val intent = Intent(it.context, WriteMemoActivity::class.java)
                intent.putExtra(FirebaseConstant.DB_MEMO_UUID, item.uuid)
                intent.putExtra(FirebaseConstant.DB_MEMO_TITLE, item.title.toString())
                intent.putExtra(FirebaseConstant.DB_MEMO_DESCRIPTION, item.description.toString())
                intent.putExtra(FirebaseConstant.DB_MEMO_LOCATION, item.location.toString())
                intent.putExtra(FirebaseConstant.DB_MEMO_CATEGORY, item.category)
                it.context.startActivity(intent)
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