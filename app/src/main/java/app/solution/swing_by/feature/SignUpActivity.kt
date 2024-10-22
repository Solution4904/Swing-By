package app.solution.swing_by.feature

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            btnConfirm.setOnClickListener { signUp() }
            btnCancel.setOnClickListener { finish() }
        }
    }

    // # 이메일 가입
    private fun signUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAPI.signUp(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(result: DataSnapshot?) {
                finish()
            }

            override fun successCallback(result: Task<AuthResult>) {}
            override fun failureCallback() {}
        })
    }
}