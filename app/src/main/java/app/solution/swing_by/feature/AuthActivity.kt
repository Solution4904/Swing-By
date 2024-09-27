package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
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
            btnGoogleAccountLinking.setOnClickListener { }
            btnKakaoAccountLinking.setOnClickListener { }
            btnNaverAccountLinking.setOnClickListener { }

            btnSignup.setOnClickListener { signUp() }

            btnSignin.setOnClickListener { signIn() }
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
                    val currentUser = Firebase.auth.currentUser
                    val currentId = currentUser?.uid.orEmpty()
                    Log.d("SOL_TAG", "currentId: $currentId")

                    val tempMemoData = mutableMapOf<String, Any>()
                    tempMemoData["title"] = currentUser?.email.toString()
                    tempMemoData["desc"] = currentId
                    tempMemoData["location"] = "생성된 장소명"

                    Firebase.database.reference.child("Users").child(currentId).updateChildren(tempMemoData)

                    val intent = Intent(this, MemoListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    it.exception?.stackTrace
                }
            }
    }
}