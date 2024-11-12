package app.solution.swing_by.feature

import app.solution.swing_by.root.MyUtils
import app.solution.swing_by.R
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
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            MyUtils.toast(this, resources.getString(R.string.please_enter_your_ID_and_password))
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