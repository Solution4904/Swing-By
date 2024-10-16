package app.solution.swing_by.feature

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "필요 항목이 입력되지 않았습니다", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                // 회원가입 성공
                if (it.isSuccessful) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    finish()
                }
                // 회원가입 실패
                else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    Log.d("SOL_LOG", "signUp: ${it.exception}")
                }
            }
    }
}