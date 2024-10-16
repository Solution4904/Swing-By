package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.MyApplication
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


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
            btnKakaoAccountLinking.setOnClickListener { kakaoSignIn() }
            btnSignup.setOnClickListener { signUp() }
            btnSignin.setOnClickListener { signIn() }
            btnTempMap.setOnClickListener {
                val intent = Intent(this@AuthActivity, MapActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) return

        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("이메일 로그인", "${it.result.user?.email} / ${it.result.user?.uid}")

                    val intent = Intent(this, MemoListActivity::class.java)
//                    intent.putExtra("UID", it.result.user?.uid)
                    MyApplication.userUid = it.result.user?.uid.toString()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    it.exception?.stackTrace
                }
            }
    }

    private fun kakaoSignIn() {
        KakaoAPI.signIn(this, object : KakaoAPI.KakaoCallBack {
            override fun successCallback() {
                val intent = Intent(this@AuthActivity, MemoListActivity::class.java)
                startActivity(intent)
            }

            override fun failureCallback() {}
        })
    }
}