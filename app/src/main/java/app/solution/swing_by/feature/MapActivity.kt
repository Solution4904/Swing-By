package app.solution.swing_by.feature

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import app.solution.swing_by.KakaoAPIService
import app.solution.swing_by.KeywordSerchingResultData
import app.solution.swing_by.NotificationManager
import app.solution.swing_by.Document
import app.solution.swing_by.constant.KakaoConstant
import app.solution.swing_by.databinding.ActivityMapBinding
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var kakaoMap: KakaoMap
    private lateinit var retrofit: Retrofit
    private val nearbySerchResults = mutableListOf<Document>()
    private var latitude: String = ""
    private var longitude: String = ""


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

            btnTracking.setOnClickListener {
                trackMyLocation()
            }

            btnSerch.setOnClickListener { serching() }
        }
    }

    private fun serching() {
        retrofit = Retrofit.Builder()
            .baseUrl(KakaoConstant.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(KakaoAPIService::class.java)
        retrofitService.getSerchingResult(query = binding.etKeyword.text.toString(), x = longitude, y = latitude).enqueue(object : Callback<KeywordSerchingResultData> {
            override fun onResponse(p0: Call<KeywordSerchingResultData>, p1: Response<KeywordSerchingResultData>) {
//                Log.d("SOL_LOG", p1.body().toString())

                nearbySerchResults.clear()
                for (document in p1.body()!!.documents) {
                    Log.d("SOL_LOG", "document\n$document")
                    nearbySerchResults.add(document)

                    // Notification Test
                    val notificationManager = NotificationManager(this@MapActivity)
                    notificationManager.showNotification(
                        binding.etKeyword.text.toString(),
                        "${document.place_name} (${document.distance}m)"
                    )
                }

//                Log.d("SOL_LOG", "검색 결과 갯수 : ${nearbySerchResults.count()}")
            }

            override fun onFailure(p0: Call<KeywordSerchingResultData>, p1: Throwable) {
                Log.d("SOL_LOG", p1.stackTrace.toString())
            }

        })
    }

    private fun moveToPosition(x: Double = 37.402005, y: Double = 127.108621) {
        latitude = x.toString()
        longitude = y.toString()

        val cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(x, y))

        kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
    }

    private fun trackMyLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            it?.let {
                Toast.makeText(this, "위치가 확인되었습니다.", Toast.LENGTH_SHORT).show()
                // TODO: lastLocation이 없는 경우 NPE으로 강제 종료되는 문제가 있음.
                // TODO: 위치 권한을 거절했던, 재설치했건 재요청하는 기능 필요.
                Log.d("SOL_LOG", "trackMyLocation: ${it.latitude}, ${it.longitude}")
                moveToPosition(it.latitude, it.longitude)
            }
        }
    }
}