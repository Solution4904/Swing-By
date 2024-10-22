package app.solution.swing_by.feature

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.solution.swing_by.MyApplication
import app.solution.swing_by.api.LocalDataConstant
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
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
        requestPermission()
        getCurrentLocation()
    }

    // # 버튼 이벤트 추가
    private fun setButtons() {
        with(binding) {
            btnKakaoAccountLinking.setOnClickListener { kakaoSignIn() }
            btnSignup.setOnClickListener { signUp() }
            btnSignin.setOnClickListener { emailSignIn() }
            btnTempMap.setOnClickListener { }
        }
    }

    // # 현재 위치 정보 불러오기
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {
                it?.let {
                    Log.d("SOL_LOG", "getCurrentLocation: ${it.longitude} / ${it.latitude}")
                }
            }
    }

    // # 필요 권한 요청
    //    @SuppressLint("MissingPermission")
    private fun requestPermission() {
        TedPermission.create().apply {
            setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(this@AuthActivity, "Permission Granted", Toast.LENGTH_SHORT).show();
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@AuthActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            })
            setDeniedMessage("서비스를 이용하시려면 위치 권한이 필요합니다.")
            setPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ).check()
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
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAPI.signIn(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback(result: Task<AuthResult>) {
                val currentUser = result.result.user

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

            override fun successCallback(result: DataSnapshot?) {}
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
            override fun failureCallback() {}
        })
    }
}