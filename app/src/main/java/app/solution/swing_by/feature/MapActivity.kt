package app.solution.swing_by.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.solution.swing_by.databinding.ActivityMapBinding
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var kakaoMap: KakaoMap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setButtons()

        binding.mapview.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {}

            override fun onMapError(p0: Exception?) {
                Log.d("SOL_LOG", "onMapError: $p0")
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                kakaoMap = p0
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.mapview.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.pause()
    }

    private fun setButtons() {
        with(binding) {
            btnMovetoposition.setOnClickListener {
                val x = etX.text.toString()
                val y = etY.text.toString()

                moveToPosition(x.toDouble(), y.toDouble())
            }

            btnTracking.setOnClickListener { trackMyLocation() }
        }
    }

    private fun moveToPosition(x: Double = 37.402005, y: Double = 127.108621) {
        val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(x.toDouble(), y.toDouble()))

        kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
    }

    private fun trackMyLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                // TODO: lastLocation이 없는 경우 NPE으로 강제 종료되는 문제가 있음.  
                Log.d("SOL_LOG", "trackMyLocation: ${it.latitude}, ${it.longitude}")
                moveToPosition(it.latitude, it.longitude)
            }
        }
    }
}