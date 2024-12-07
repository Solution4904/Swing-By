package app.solution.swing_by.feature

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.get
import app.solution.swing_by.R
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.constant.KakaoCategoryGroupCode
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import app.solution.swing_by.root.MyUtils
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowOrientationRules
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAlign
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
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
            btnLocationTooltip.setOnClickListener {
                showTooltip(
                    it, getString(R.string.location_tooltip),
                    SpannableStringData(
                        word = "때문에 상호명이 아닌 지점명과 같은 다른 지점과 동일하지 않은 이름으로 입력 시 원활한 탐색이 어려울 수 있습니다.",
                        color = getColor(R.color.personalBlue)
                    )
                )
            }
            btnCategoryTooltip.setOnClickListener {
                showTooltip(
                    it, getString(R.string.category_tooltip),
                    SpannableStringData(
                        word = "만약 장소 명이 정확하더라도 카테고리가 다르다면 검색되지 않습니다.",
                        color = getColor(R.color.personalBlue)
                    )
                )
            }
        }
    }

    // # 항목 별 툴팁 표시
    private fun showTooltip(view: View, description: String, spannableStringData: SpannableStringData) {
        val _start = description.indexOf(spannableStringData.word)
        val _end = _start + spannableStringData.word.length

        val spannableString = SpannableString(description).apply {
            setSpan(
                ForegroundColorSpan(spannableStringData.color),
                _start,
                _end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                StyleSpan(Typeface.BOLD),
                _start,
                _end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val balloon = Balloon.Builder(this@WriteMemoActivity)
            .setWidthRatio(0.8f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(spannableString)
            .setTextColorResource(R.color.personalBlack)
            .setTextSize(13f)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)         // 화살표 위치 (클릭한 곳 가리킴)
            .setArrowOrientationRules(ArrowOrientationRules.ALIGN_FIXED)    // 화살표 위, 아래
            .setArrowOrientation(ArrowOrientation.TOP)                      // 화살표 방향
            .setArrowSize(20)
            .setArrowPosition(0.5f)
            .setPadding(12)
            .setCornerRadius(10f)
            .setTextGravity(Gravity.START)
            .setBackgroundColorResource(R.color.personalWhite)
            .setBalloonAnimation(BalloonAnimation.OVERSHOOT)
            .setLifecycleOwner(this@WriteMemoActivity)
            .build()
        balloon.showAlign(BalloonAlign.BOTTOM, view, xOff = 100)
        balloon.dismissWithDelay(60000)
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
        )

        FirebaseAPI.registerMemo(memoUUID, memoModel, object : FirebaseAPI.Callback {
            override fun successCallback() {
                finish()
            }

            override fun failureCallback() {}
        })
    }
}

data class SpannableStringData(
    val word: String,
    val color: Int,
)