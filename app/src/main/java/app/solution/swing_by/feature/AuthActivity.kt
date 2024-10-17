package app.solution.swing_by.feature

import android.Manifest
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
import app.solution.swing_by.databinding.ActivityAuthBinding
import com.google.android.gms.location.LocationServices


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
//                val intent = Intent(this@AuthActivity, MapActivity::class.java)
//                startActivity(intent)
//                KakaoAPI.serching(this@AuthActivity, this@AuthActivity, "편의점")
                temp()
            }
        }
    }

    private fun temp() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 권한이 없을 경우
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 거부 이력이 있을 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d("SOL_LOG", "AAAAA")

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 100
                )
            }
            // 권한 2회 이상 거부했을 경우
            else {
                Log.d("SOL_LOG", "BBBBB")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 100
                )
            }
        }
        // 권한이 있을 경우
        else {
            Log.d("SOL_LOG", "CCCCC")
            fusedLocationClient.lastLocation.addOnSuccessListener { result ->
                result.let {
                    Log.d("SOL_LOG", "DDDDD")
                    // TODO: lastLocation이 없는 경우 NPE으로 강제 종료되는 문제가 있음.
                    // TODO: 위치 권한을 거절했던, 재설치했건 재요청하는 기능 필요.

                    Log.d("SOL_LOG", "trackingMyLocation: ${result.longitude} / ${result.latitude}")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            100 -> {
                // 권한 요청 후 승인하면 옴
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("SOL_LOG", "EEEEE")

                }
                // 권한 거부
                else {
                    Log.d("SOL_LOG", "FFFFF")

                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher_foreground)
                        .setTitle("필수 권한")
                        .setMessage("위치 권한을 허용해야만 이용 가능합니다.")
                        .setPositiveButton("설정") { _, _ ->
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.parse("package:${this.packageName}"))
                                startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                                startActivity(intent)
                            }
                        }
                        .setNegativeButton("거부") { _, _ ->
                            finishAffinity()
                        }
                        .create()
                        .show()
                }
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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAPI.signIn(email, password, object : FirebaseAPI.FirebaseCallback {
            override fun successCallback() {
                val intent = Intent(this@AuthActivity, MemoListActivity::class.java)
                startActivity(intent)
            }

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