package app.solution.swing_by.feature

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.get
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.constant.KakaoCategoryGroupCode
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import app.solution.swing_by.root.MyUtils
import java.util.UUID

class WriteMemoActivity : BaseActivity<ActivityWriteMemoBinding>(ActivityWriteMemoBinding::inflate) {
    private lateinit var memoUUID: String


    override fun initView() {
        super.initView()

        getPreviousData()
    }

    override fun initListener() {
        super.initListener()

        setButtons()
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
    @RequiresApi(Build.VERSION_CODES.O)
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
            },
            FirebaseConstant.DB_MEMO_CURRENT_TIME to MyUtils.getDateTime()

            /*with(FirebaseConstant) {
                DB_MEMO_UUID to memoUUID
                DB_MEMO_DESCRIPTION to binding.etDescription.text.toString()
                DB_MEMO_LOCATION to binding.etLocation.text.toString()
                DB_MEMO_CATEGORY to binding.chipGroup.checkedChipId
                DB_MEMO_CATEGORY_CODE to when (binding.chipGroup.checkedChipId) {
                    2131230869 -> KakaoCategoryGroupCode.MT1
                    2131230865 -> KakaoCategoryGroupCode.CS2
                    2131230864 -> KakaoCategoryGroupCode.BK9
                    2131230866 -> KakaoCategoryGroupCode.PO3
                    2131230870 -> KakaoCategoryGroupCode.FD6
                    2131230863 -> KakaoCategoryGroupCode.CE7
                    2131230868 -> KakaoCategoryGroupCode.PM9
                    else -> ""
                }
                DB_MEMO_CURRENT_TIME to System.currentTimeMillis().toString()
            }*/
        )

        FirebaseAPI.registerMemo(memoUUID, memoModel, object : FirebaseAPI.Callback {
            override fun successCallback() {
                finish()
            }

            override fun failureCallback() {}
        })
    }
}
