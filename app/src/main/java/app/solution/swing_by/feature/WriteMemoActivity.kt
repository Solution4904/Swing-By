package app.solution.swing_by.feature

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityWriteMemoBinding
import com.google.android.material.chip.Chip

class WriteMemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWriteMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWriteMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setChipButtons()
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
}
