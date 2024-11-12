package app.solution.swing_by.feature

import android.content.Intent
import app.solution.swing_by.root.MyApplication
import app.solution.swing_by.root.MyUtils
import app.solution.swing_by.R
import app.solution.swing_by.constant.LocalDataConstant
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.common.ProgressView
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.kakao.sdk.user.model.AccessTokenInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AuthActivity : BaseActivity<ActivityAuthBinding>(ActivityAuthBinding::inflate) {
    private val progressView: ProgressView by lazy {
        ProgressView(this, this).create(this, this)
    }


    override fun initView() {
        super.initView()

        binding.root.addView(progressView)
    }

    override fun initListener() {
        super.initListener()

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
        progressView.show()

        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            MyUtils.toast(this, resources.getString(R.string.please_enter_your_ID_and_password))

            progressView.hide()
            return
        }

        FirebaseAPI.signIn(email, password, object : FirebaseAPI.CallbackByAuthResult {
            override fun successCallback(results: Task<AuthResult>) {
                val currentUser = results.result.user

                currentUser?.let { user ->
                    CoroutineScope(Dispatchers.Main).launch {
                        MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.UID, user.uid)
                    }.invokeOnCompletion {
                        progressView.hide()

                        Intent(this@AuthActivity, MemoListActivity::class.java).apply {
                            startActivity(this)
                        }
                    }
                }
            }

            override fun failureCallback() {}
        })
    }

    // # 카카오 계정 간편 로그인
    private fun kakaoSignIn() {
        progressView.show()

        KakaoAPI.signIn(this, object : KakaoAPI.CallbackByAccessTokenInfo {
            override fun successCallback(accessTokenInfo: AccessTokenInfo?) {
                CoroutineScope(Dispatchers.Main).launch {
                    MyApplication.getInstance().getLocalDataManager().setString(LocalDataConstant.UID, accessTokenInfo?.id.toString())
                }.invokeOnCompletion {
                    progressView.hide()

                    Intent(this@AuthActivity, MemoListActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }

            override fun failureCallback() {}
        })
    }
}