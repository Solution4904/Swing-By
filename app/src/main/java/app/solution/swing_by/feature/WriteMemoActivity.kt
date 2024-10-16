package app.solution.swing_by.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import java.util.UUID

class WriteMemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteMemoBinding
    private lateinit var memoUUID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
        getPreviousData()
    }

    private fun getPreviousData() {
        memoUUID = intent.getStringExtra(FirebaseConstant.DB_MEMO_UUID) ?: UUID.randomUUID().toString()

        with(binding) {
            etTitle.apply { setText(intent.getStringExtra(FirebaseConstant.DB_MEMO_TITLE) ?: "") }
            etDescription.apply { setText(intent.getStringExtra(FirebaseConstant.DB_MEMO_DESCRIPTION) ?: "") }
            etLocation.apply { setText(intent.getStringExtra(FirebaseConstant.DB_MEMO_LOCATION) ?: "") }
            chipGroup.apply {
                if (intent.hasExtra(FirebaseConstant.DB_MEMO_CATEGORY))
                    chipGroup.check(intent.getIntExtra(FirebaseConstant.DB_MEMO_CATEGORY, chipGroup[0].id))
            }
        }
    }

    private fun setButtons() {
        with(binding) {
            btnConfirm.setOnClickListener { registerMemo() }
            btnCancel.setOnClickListener { finish() }
        }
    }

    private fun registerMemo() {
        val memoModel = mutableMapOf<String, Any>(
            FirebaseConstant.DB_MEMO_UUID to memoUUID,
            FirebaseConstant.DB_MEMO_TITLE to binding.etTitle.text.toString(),
            FirebaseConstant.DB_MEMO_DESCRIPTION to binding.etDescription.text.toString(),
            FirebaseConstant.DB_MEMO_LOCATION to binding.etLocation.text.toString(),
            FirebaseConstant.DB_MEMO_CATEGORY to binding.chipGroup.checkedChipId,
        )

        FirebaseAPI.registerMemo(memoUUID, memoModel, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback() {
//                Toast.makeText(this@WriteMemoActivity, "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun failureCallback() {
//                Toast.makeText(this@WriteMemoActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
