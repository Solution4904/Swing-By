package app.solution.swing_by.feature

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.solution.swing_by.R
import app.solution.swing_by.api.FirebaseAPI
import app.solution.swing_by.api.KakaoAPI
import app.solution.swing_by.constant.KakaoConstant
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission


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
                permission()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun permission() {
        TedPermission.create().apply {
            setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    Toast.makeText(this@AuthActivity, "Permission Granted", Toast.LENGTH_SHORT).show();
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@AuthActivity)
                    fusedLocationClient.lastLocation.addOnSuccessListener { result ->
                        result.let {
                            Log.d("SOL_LOG", "DDDDD")
                            // TODO: lastLocation이 없는 경우 NPE으로 강제 종료되는 문제가 있음.
                            // TODO: 위치 권한을 거절했던, 재설치했건 재요청하는 기능 필요.

                            Log.d("SOL_LOG", "trackingMyLocation: ${result.longitude} / ${result.latitude}")
                        }
                    }
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

    private fun signUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAPI.signIn(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback() {
                val intent = Intent(this@AuthActivity, MemoListActivity::class.java)
                startActivity(intent)
            }

            override fun successCallback(result: DataSnapshot) {}
            override fun failureCallback() {}
        })
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