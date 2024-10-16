package app.solution.swing_by.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient


class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var currentUid: String

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
                val intent = Intent(baseContext, MapActivity::class.java)
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
                    intent.putExtra("UID", it.result.user?.uid)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    it.exception?.stackTrace
                }
            }
    }

    private fun kakaoSignIn() {
//        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
//            if (error != null) {
//                Log.e("SOL_LOG", "로그인 실패", error)
//            } else if (token != null) {
//                Log.i("SOL_LOG", "로그인 성공 ${token.accessToken}")
//            }
//        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Log.d("[카카오로그인]", "접근이 거부 됨(동의 취소)")
                    }

                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Log.d("[카카오로그인]", "유효하지 않은 앱")
                    }

                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Log.d("[카카오로그인]", "인증 수단이 유효하지 않아 인증할 수 없는 상태")
                    }

                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Log.d("[카카오로그인]", "요청 파라미터 오류")
                    }

                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Log.d("[카카오로그인]", "유효하지 않은 scope ID")
                    }

                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Log.d("[카카오로그인]", "설정이 올바르지 않음(android key hash)")
                    }

                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Log.d("[카카오로그인]", "서버 내부 에러")
                    }

                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Log.d("[카카오로그인]", "앱이 요청 권한이 없음")
                    }

                    else -> { // Unknown
                        Log.d("[카카오로그인]", "기타 에러")
                    }
                }
            } else if (token != null) {
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    UserApiClient.instance.me { user, error ->
                        Log.d("[카카오로그인]", "로그인에 성공하였습니다.\n${token.accessToken}\n\"닉네임: ${user?.kakaoAccount?.profile?.nickname}\"\nUID: ${tokenInfo?.id.toString()}")

                        val intent = Intent(this, MemoListActivity::class.java)
                        intent.putExtra("UID", tokenInfo?.id.toString())
                        startActivity(intent)
                    }
                }
            } else {
                Log.d("카카오로그인", "토큰==null error==null")
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e("SOL_LOG", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        Log.d("SOL_LOG", "카카오계정 로그인 의도적 취소")
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                } else if (token != null) {
                    Log.i("SOL_LOG", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
        }
    }
}