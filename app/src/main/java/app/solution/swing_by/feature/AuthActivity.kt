package app.solution.swing_by.feature

import android.content.Intent
import app.solution.swing_by.root.MyApplication
import app.solution.swing_by.root.MyUtils
import app.solution.swing_by.R
import app.solution.swing_by.constant.LocalDataConstant
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.common.ProgressView
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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

        CoroutineScope(Dispatchers.Main).launch {
            progressView.show()

            hasLoggedInBefore()
        }
    }

    override fun initListener() {
        super.initListener()

        setButtons()
    }

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            btnSignup.setOnClickListener { signUp() }
            btnSignin.setOnClickListener { signIn() }
        }
    }

    // # 이메일 계정 가입
    private fun signUp() {
        Intent(this, SignUpActivity::class.java).apply {
            startActivity(this)
        }
    }

    // # 로그인 이력 확인
    private suspend fun hasLoggedInBefore() {
        if (MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.UID).isNotBlank()) {
            signIn(
                AuthData(
                    MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.ID),
                    MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.PASSWORD)
                )
            )
        } else {
            progressView.hide()
        }
    }


    // # 계정 로그인
    private fun signIn(authData: AuthData? = null) {
        progressView.show()

        val _authData = authData ?: AuthData(binding.etEmail.text.toString(), binding.etPassword.text.toString())

        if (authData == null && _authData.id.isEmpty() ||
            authData == null && _authData.password.isEmpty()
        ) {
            MyUtils.toast(this, resources.getString(R.string.please_enter_your_ID_and_password))

            progressView.hide()
            return
        }

        FirebaseAPI.signIn(_authData.id, _authData.password, object : FirebaseAPI.CallbackByAuthResult {
            override fun successCallback(results: Task<AuthResult>) {
                val currentUser = results.result.user

                currentUser?.let { user ->
                    CoroutineScope(Dispatchers.Main).launch {
                        if (MyApplication.getInstance().getLocalDataManager().getString(LocalDataConstant.UID).isBlank())
                            with(MyApplication.getInstance().getLocalDataManager()) {
                                setString(LocalDataConstant.UID, user.uid)
                                setString(LocalDataConstant.ID, _authData.id)
                                setString(LocalDataConstant.PASSWORD, _authData.password)
                            }

                    }.invokeOnCompletion {
                        Intent(this@AuthActivity, MemoListActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    }
                }
            }

            override fun failureCallback() {
                progressView.hide()
            }
        })
    }
}

data class AuthData(
    val id: String,
    val password: String,
)