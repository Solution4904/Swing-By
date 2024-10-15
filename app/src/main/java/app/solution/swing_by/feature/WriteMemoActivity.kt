package app.solution.swing_by.feature

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import app.solution.swing_by.MyApplication
import app.solution.swing_by.constant.FirebaseConstant
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
        Log.d("SOL_LOG", "memoUUID -> ${memoUUID}")

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
        val memoModel = mutableMapOf<String, Any>()
        memoModel[FirebaseConstant.DB_MEMO_UUID] = memoUUID
        memoModel[FirebaseConstant.DB_MEMO_TITLE] = binding.etTitle.text.toString()
        memoModel[FirebaseConstant.DB_MEMO_DESCRIPTION] = binding.etDescription.text.toString()
        memoModel[FirebaseConstant.DB_MEMO_LOCATION] = binding.etLocation.text.toString()
        memoModel[FirebaseConstant.DB_MEMO_CATEGORY] = binding.chipGroup.checkedChipId

        Firebase.database.reference.child(FirebaseConstant.DB_MEMOLIST).child(MyApplication.userUid).child(memoUUID).setValue(memoModel)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "오류가 발생했습니다..", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
