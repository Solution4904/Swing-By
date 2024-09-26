package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    private fun setButtons() {
        with(binding) {
            btnGoogleAccountLinking.setOnClickListener { }
            btnKakaoAccountLinking.setOnClickListener { }
            btnNaverAccountLinking.setOnClickListener { }

            btnSignup.setOnClickListener { signUp() }

            btnSignin.setOnClickListener { }
        }
    }

    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}