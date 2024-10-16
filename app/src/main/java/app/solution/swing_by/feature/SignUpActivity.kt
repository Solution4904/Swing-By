package app.solution.swing_by.feature

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    private fun setButtons() {
        with(binding) {
            btnConfirm.setOnClickListener { signUp() }
            btnCancel.setOnClickListener { finish() }
        }
    }

    private fun signUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAPI.signUp(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback() {
                finish()
            }

            override fun failureCallback() {}
        })
    }
}