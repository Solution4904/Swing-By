package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.Document
import app.solution.swing_by.MyApplication
import app.solution.swing_by.MyUtils
import app.solution.swing_by.constant.LocalDataConstant
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.kakao.sdk.user.model.AccessTokenInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()
    }

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            btnKakaoAccountLinking.setOnClickListener { kakaoSignIn() }
            btnSignup.setOnClickListener { signUp() }
            btnSignin.setOnClickListener { emailSignIn() }
            btnTempMap.setOnClickListener {
                Intent(this@AuthActivity, MapActivity::class.java).apply {
                    startActivity(this)
                }
            }
        }
    }

    // # 이메일 계정 가입
    private fun signUp() {
        Intent(this, SignUpActivity::class.java).apply {
            startActivity(this)
        }
    }

    // # 이메일 계정 로그인
    private fun emailSignIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            MyUtils.toast(this, "이메일과 비밀번호를 입력해주세요.")
            return
        }

        FirebaseAPI.signIn(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(results: Task<AuthResult>) {
                val currentUser = results.result.user

                currentUser?.let { user ->
                    CoroutineScope(Dispatchers.Main).launch {
                        MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.UID, user.uid)
                    }.invokeOnCompletion {
                        Intent(this@AuthActivity, MemoListActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                }
            }

            override fun successCallback(results: DataSnapshot?) {}
            override fun failureCallback() {}
        })
    }

    // # 카카오 계정 간편 로그인
    private fun kakaoSignIn() {
        KakaoAPI.signIn(this, object : KakaoAPI.KakaoCallBack {
            override fun successCallback(accessTokenInfo: AccessTokenInfo?) {
                CoroutineScope(Dispatchers.Main).launch {
                    MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.UID, accessTokenInfo?.id.toString())
                }.invokeOnCompletion {
                    Intent(this@AuthActivity, MemoListActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }

            override fun successCallback() {}
            override fun successCallback(array: Array<Document>) {}
            override fun failureCallback() {}
        })
    }
}