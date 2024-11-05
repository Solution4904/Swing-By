package app.solution.swing_by.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.constant.KakaoCategoryGroupCode
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
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

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            btnConfirm.setOnClickListener { registerMemo() }
            btnCancel.setOnClickListener { finish() }
        }
    }

    // # 메모 수정 진입 시 데이터 세팅
    private fun getPreviousData() {
        memoUUID = intent.getStringExtra(FirebaseConstant.DB_MEMO_UUID) ?: UUID.randomUUID().toString()

        with(binding) {
            etDescription.apply { setText(intent.getStringExtra(FirebaseConstant.DB_MEMO_DESCRIPTION) ?: "") }
            etLocation.apply { setText(intent.getStringExtra(FirebaseConstant.DB_MEMO_LOCATION) ?: "") }
            chipGroup.apply {
                if (intent.hasExtra(FirebaseConstant.DB_MEMO_CATEGORY))
                    chipGroup.check(intent.getIntExtra(FirebaseConstant.DB_MEMO_CATEGORY, chipGroup[0].id))
            }
        }
    }

    // # 메모 등록
    private fun registerMemo() {
        val memoModel = mutableMapOf<String, Any>(
            FirebaseConstant.DB_MEMO_UUID to memoUUID,
            FirebaseConstant.DB_MEMO_DESCRIPTION to binding.etDescription.text.toString(),
            FirebaseConstant.DB_MEMO_LOCATION to binding.etLocation.text.toString(),
            FirebaseConstant.DB_MEMO_CATEGORY to binding.chipGroup.checkedChipId,
            FirebaseConstant.DB_MEMO_CATEGORY_CODE to when (binding.chipGroup.checkedChipId) {
                2131230869 -> KakaoCategoryGroupCode.MT1
                2131230865 -> KakaoCategoryGroupCode.CS2
                2131230864 -> KakaoCategoryGroupCode.BK9
                2131230866 -> KakaoCategoryGroupCode.PO3
                2131230870 -> KakaoCategoryGroupCode.FD6
                2131230863 -> KakaoCategoryGroupCode.CE7
                2131230868 -> KakaoCategoryGroupCode.PM9
                else -> ""
            }
        )

        FirebaseAPI.registerMemo(memoUUID, memoModel, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(results: DataSnapshot?) {
                finish()
            }

            override fun successCallback(results: Task<AuthResult>) {}
            override fun failureCallback() {}
        })
    }
}
