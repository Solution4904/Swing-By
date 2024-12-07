package app.solution.swing_by.feature

import app.solution.swing_by.root.MyUtils
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.base.BaseActivity
import app.solution.swing_by.databinding.ActivitySignupBinding

class SignUpActivity : BaseActivity<ActivitySignupBinding>(ActivitySignupBinding::inflate) {
    override fun initListener() {
        super.initListener()

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
        fun checkEmail(email: String): Boolean {
            if (email.contains("@")
                && email.contains(".")
                && email.length > 5
            ) {
                return true
            } else {
                MyUtils.toast(this@SignUpActivity, "올바른 이메일 아이디를 입력해주세요")
                return false
            }
        }

        fun checkPassword(password: String): Boolean {
            if (password.length >= 6) return true
            else {
                MyUtils.toast(this@SignUpActivity, "비밀번호는 6글자 이상이어야 합니다")
                return false
            }
        }

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!checkEmail(email) || !checkPassword(password)) {
            return
        }

        FirebaseAPI.signUp(email, password, object : FirebaseAPI.Callback {
            override fun successCallback() {
                finish()
            }

            override fun failureCallback() {}
        })
    }
}