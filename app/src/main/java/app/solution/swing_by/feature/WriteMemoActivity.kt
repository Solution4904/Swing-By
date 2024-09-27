package app.solution.swing_by.feature

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WriteMemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setChipButtons()
        setButtons()
    }

    private fun setChipButtons() {
        fun checkedAndClearing(view: View) {
            binding.chipGroup.clearCheck()

            val chip = view as Chip
            chip.isChecked = true
        }

        with(binding) {
            chipBank.setOnClickListener { checkedAndClearing(it) }
            chipMart.setOnClickListener { checkedAndClearing(it) }
            chipMiscellaneous.setOnClickListener { checkedAndClearing(it) }
            chipClothes.setOnClickListener { checkedAndClearing(it) }
            chipErrands.setOnClickListener { checkedAndClearing(it) }
            chipRestaurant.setOnClickListener { checkedAndClearing(it) }
            chipCosmetic.setOnClickListener { checkedAndClearing(it) }
            chipEtc.setOnClickListener { checkedAndClearing(it) }
        }
    }

    private fun setButtons() {
        with(binding) {
            btnConfirm.setOnClickListener { registerMemo() }
            btnCancel.setOnClickListener { finish() }
        }
    }

    private fun registerMemo() {
        val currentUser = Firebase.auth.currentUser
        val currentUid = currentUser?.uid.orEmpty()

        val memoModel = mutableMapOf<String, Any>()
        memoModel["title"] = binding.etTitle.text.toString()
        memoModel["desc"] = binding.etDesc.text.toString()
        memoModel["location"] = binding.etLocation.text.toString()

        Firebase.database.reference.child(currentUid).updateChildren(memoModel)
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
