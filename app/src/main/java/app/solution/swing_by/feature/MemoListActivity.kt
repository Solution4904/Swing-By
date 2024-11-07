package app.solution.swing_by.feature

import android.app.AlertDialog
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.swing_by.MyUtils
import app.solution.swing_by.R
import app.solution.swing_by.adapter.MemoListAdapter
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.databinding.ActivityMemoListBinding
import app.solution.swing_by.item.MemoItem
import com.google.firebase.database.DataSnapshot


class MemoListActivity : BaseActivity<ActivityMemoListBinding>(ActivityMemoListBinding::inflate) {
    private lateinit var memoListAdapter: MemoListAdapter


    override fun initListener() {
        super.initListener()

        setButtons()
    }

    override fun refreshView() {
        super.refreshView()

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
        val callback = object : FirebaseAPI.CallbackByDataSnapshot {
            override fun successCallback(results: DataSnapshot?) {
                results?.let {
                    val elements = it.children.map { snapshot ->
                        snapshot.getValue(MemoItem::class.java)
                    }
                    memoListAdapter.submitList(elements.toMutableList())
                }
            }

            override fun failureCallback() {}
        }
        FirebaseAPI.getMemoList(callback)

        memoListAdapter = MemoListAdapter {
            AlertDialog.Builder(this)
                .setTitle(resources.getString(R.string.delete_a_memo))
                .setMessage("[${it.location}] \n${it.description}")
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    FirebaseAPI.deleteMemo(it.uuid.toString(), callback)
                    MyUtils.toast(this@MemoListActivity, resources.getString(R.string.memo_deleted))
                }
                .setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                    MyUtils.toast(this@MemoListActivity, resources.getString(R.string.memo_deleted_cancel))
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